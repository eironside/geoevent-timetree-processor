package com.esri.geoevent.processor.timetree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.esri.ges.core.property.LabeledValue;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class TimetreeProcessorDefinition extends GeoEventProcessorDefinitionBase implements TimetreeProperties
{
  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(TimetreeProcessorDefinition.class);

  public TimetreeProcessorDefinition()
  {
    try
    {
      final List<LabeledValue> allowedTimeUnits = new ArrayList<>();
      for (TimeUnit value : TimeUnit.values())
      {
        allowedTimeUnits.add(new LabeledValue(value.name(), value.name()));
      }

      final List<LabeledValue> allowedTimeFields = new ArrayList<>();
      allowedTimeFields.add(new LabeledValue(TIME_START, TIME_START));
      allowedTimeFields.add(new LabeledValue(TIME_END, TIME_END));
      allowedTimeFields.add(new LabeledValue(RECEIVED_TIME, RECEIVED_TIME));

      propertyDefinitions.put(IS_DELAY_COUNT, new PropertyDefinition(IS_DELAY_COUNT, PropertyType.Boolean, true, IS_DELAY_COUNT_LABEL, IS_DELAY_COUNT_DESC, true, false));

      propertyDefinitions.put(DELAY_VALUE, new PropertyDefinition(DELAY_VALUE, PropertyType.Long, "5", DELAY_VALUE_LABEL, DELAY_VALUE_DESC, true, false));

      propertyDefinitions.put(DELAY_VALUE_UNITS, new PropertyDefinition(DELAY_VALUE_UNITS, PropertyType.String, TimeUnit.SECONDS.name(), DELAY_VALUE_UNITS_LABEL, DELAY_VALUE_UNITS_DESC, IS_DELAY_COUNT + "=false", true, false, allowedTimeUnits));

      propertyDefinitions.put(DELAY_FIELD, new PropertyDefinition(DELAY_FIELD, PropertyType.String, TIME_START, DELAY_FIELD_LABEL, DELAY_FIELD_DESC, IS_DELAY_COUNT + "=false", true, false, allowedTimeFields));

      propertyDefinitions.put(CLEAR_CACHE, new PropertyDefinition(CLEAR_CACHE, PropertyType.Boolean, false, CLEAR_CACHE_LABEL, CLEAR_CACHE_DESC, true, false));
    }
    catch (PropertyException e)
    {
      LOGGER.warn("Failed to construct definition.", e);
    }
  }

  @Override
  public String getName()
  {
    return PROCESSOR_NAME;
  }

  @Override
  public String getDomain()
  {
    return PROCESSOR_DOMAIN;
  }

  @Override
  public String getDescription()
  {
    return PROCESSOR_DESC;
  }

  @Override
  public String getLabel()
  {
    return PROCESSOR_LABEL;
  }
}
