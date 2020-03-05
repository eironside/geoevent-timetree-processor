package com.esri.geoevent.processor.timetree;

public interface TimetreeProperties
{
  public static final String STRINGS_PATH            = "com.esri.geoevent.processor.timetree-processor";

  static final String        PROCESSOR_NAME          = "${" + STRINGS_PATH + ".PROCESSOR_NAME}";
  static final String        PROCESSOR_LABEL         = "${" + STRINGS_PATH + ".PROCESSOR_LABEL}";
  static final String        PROCESSOR_DESC          = "${" + STRINGS_PATH + ".PROCESSOR_DESC}";
  static final String        PROCESSOR_DOMAIN        = "${" + STRINGS_PATH + ".PROCESSOR_DOMAIN}";

  static final String        TIME_START              = "TIME_START";
  static final String        TIME_END                = "TIME_END";
  static final String        RECEIVED_TIME           = "RECEIVED_TIME";

  static final String        IS_DELAY_COUNT          = "isDelayCount";
  static final String        IS_DELAY_COUNT_LABEL    = "${" + STRINGS_PATH + ".IS_DELAY_COUNT_LABEL}";
  static final String        IS_DELAY_COUNT_DESC     = "${" + STRINGS_PATH + ".IS_DELAY_COUNT_DESC}";

  static final String        DELAY_VALUE             = "delayValue";
  static final String        DELAY_VALUE_LABEL       = "${" + STRINGS_PATH + ".DELAY_VALUE_LABEL}";
  static final String        DELAY_VALUE_DESC        = "${" + STRINGS_PATH + ".DELAY_VALUE_DESC}";

  static final String        DELAY_VALUE_UNITS       = "delayValueUnits";
  static final String        DELAY_VALUE_UNITS_LABEL = "${" + STRINGS_PATH + ".DELAY_VALUE_UNITS_LABEL}";
  static final String        DELAY_VALUE_UNITS_DESC  = "${" + STRINGS_PATH + ".DELAY_VALUE_UNITS_DESC}";

  static final String        DELAY_FIELD             = "delayField";
  static final String        DELAY_FIELD_LABEL       = "${" + STRINGS_PATH + ".DELAY_FIELD_LABEL}";
  static final String        DELAY_FIELD_DESC        = "${" + STRINGS_PATH + ".DELAY_FIELD_DESC}";

  static final String        CLEAR_CACHE             = "clearCache";
  static final String        CLEAR_CACHE_DESC        = "${" + STRINGS_PATH + ".CLEAR_CACHE_DESC}";
  static final String        CLEAR_CACHE_LABEL       = "${" + STRINGS_PATH + ".CLEAR_CACHE_LABEL}";

}
