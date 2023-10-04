package app.freerouting.management;

import app.freerouting.logger.FRLogger;
import app.freerouting.management.segment.Properties;
import app.freerouting.management.segment.Traits;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FRAnalytics {

  private static SegmentClient analytics;

  private static String permanent_user_id;

  public static void set_writeKey(String writeKey)
  {
    analytics = new SegmentClient(writeKey);
  }

  public static void set_userId(String userId)
  {
    permanent_user_id = userId;
  }

  private static void identifyUser(String userId, Map<String, String> traits)
  {
    try {
      Traits t = new Traits();
      t.putAll(traits);

      analytics.identify(userId, null, t);
    } catch (Exception e) {
      FRLogger.error("Exception in FRAnalytics.identifyUser: " + e.getMessage(), e);
    }
  }
  private static void identifyAnonymous(String anonymousId, Map<String, String> traits)
  {
    try {
      Traits t = new Traits();
      t.putAll(traits);

      analytics.identify(null, anonymousId, t);
    } catch (Exception e) {
      FRLogger.error("Exception in FRAnalytics.identifyAnonymous: " + e.getMessage(), e);
    }
  }

  private static void trackAnonymousAction(String anonymousId, String action, Map<String, String> properties)
  {
    try {
      Properties p = new Properties();
      p.putAll(properties);

      analytics.track(null, anonymousId, action, p);
    } catch (Exception e) {
      FRLogger.error("Exception in FRAnalytics.trackAnonymousAction: " + e.getMessage(), e);
    }
  }

  public static void identify()
  {
    Map<String, String> traits = new HashMap<>();
    traits.put("anonymous", "true");
    //identifyUser(permament_user_id, traits);
    identifyAnonymous(permanent_user_id, traits);
  }

  public static void app_start(String freeroutingVersion, String freeroutingBuildDate, String commandLineArguments,
      String osName, String osArchitecture, String osVersion,
      String javaVersion, String javaVendor,
      Locale systemLanguage, Locale guiLanguage,
      int cpuCoreCount, long ramAmount,
      Instant currentUtcTime)
  {
    Map<String, String> properties = new HashMap<>();
    properties.put("Build.Version", freeroutingVersion);
    properties.put("Build.Date", freeroutingBuildDate);
    properties.put("CommandLineArguments", commandLineArguments);
    properties.put("OS.Name", osName);
    properties.put("OS.Architecture", osArchitecture);
    properties.put("OS.Version", osVersion);
    properties.put("Java.Version", javaVersion);
    properties.put("Java.Vendor", javaVendor);
    properties.put("Language.System", systemLanguage.toString());
    properties.put("Language.GUI", guiLanguage.toString());
    properties.put("CPU.CoreCount", Integer.toString(cpuCoreCount));
    properties.put("RAM.Amount", Long.toString(ramAmount));
    properties.put("CurrentTime.UTC", currentUtcTime.toString());
    trackAnonymousAction(permanent_user_id, "Application Started", properties);
  }
}