package com.esri.geoevent.processor.timetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventPropertyName;
import com.esri.ges.core.property.Property;
import com.esri.ges.core.validation.ValidationException;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.messaging.EventDestination;
import com.esri.ges.messaging.EventUpdatable;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.GeoEventProducer;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;

/**
 * The TimetreeProcessor class processes incoming GeoEvents and delaying their execution by a specified time. Delayed
 * events are placed in a private queue and processed based on a combination of the delay time and the time they were
 * received by GeoEvent Server (the RECEIVED_TIME property).
 */
public class TimetreeProcessor extends GeoEventProcessorBase implements GeoEventProducer, EventUpdatable, TimetreeProperties
{
  private static final BundleLogger      LOGGER             = BundleLoggerFactory.getLogger(TimetreeProcessor.class);
  private static final int               MAX_ENTRIES        = 20000;

  private final Map<String, TimeTreeMap> geoEventTimeKeySet = Collections.synchronizedMap(new LinkedHashMap<String, TimeTreeMap>()
                                                              {
                                                                private static final long serialVersionUID = 3497816525702669924L;

                                                                protected boolean removeEldestEntry(Map.Entry<String, TimeTreeMap> eldest)
                                                                {
                                                                  return size() > MAX_ENTRIES;
                                                                }
                                                              });

  private Messaging                      messaging;
  private GeoEventCreator                geoEventCreator;
  private GeoEventProducer               geoEventProducer;

  private boolean                        isDelayCount       = false;
  private short                          delayCount;
  private long                           delayValue;
  private TimeUnit                       delayValueUnit;
  private long                           delayMilliseconds  = 0;
  private String                         delayField         = RECEIVED_TIME;

  public TimetreeProcessor(GeoEventProcessorDefinition definition) throws ComponentException
  {
    super(definition);
  }

  @Override
  public GeoEvent process(GeoEvent geoEvent) throws Exception
  {
    GeoEvent result = null;
    if (geoEvent != null)
    {
      GeoEvent geoEventCopy = geoEventCreator.create(geoEvent.getGeoEventDefinition().getGuid(), geoEvent.getAllFields());
      if (LOGGER.isTraceEnabled())
      {
        if (isDelayCount)
          LOGGER.trace("Processing event with Count={0}: {1}", delayCount, geoEventCopy);
        else
          LOGGER.trace("Processing event with {2} time window={0} {1}: {3}", delayValue, delayValueUnit, delayField, geoEventCopy);
      }

      final TimetreeGeoEvent delayedGeoEvent = new TimetreeGeoEvent(geoEventCopy, delayField);
      TimeTreeMap timetreeMap = geoEventTimeKeySet.get(delayedGeoEvent.getTrackKey());
      if (timetreeMap == null)
      {
        if (isDelayCount)
          timetreeMap = new TimeTreeMap(delayCount);
        else
          timetreeMap = new TimeTreeMap(delayMilliseconds);
        geoEventTimeKeySet.put(delayedGeoEvent.getTrackKey(), timetreeMap);
      }
      timetreeMap.put(delayedGeoEvent.getTime(), delayedGeoEvent);

      if (timetreeMap.keySet().size() > 1)
      {
        if (isDelayCount)
          geoEvent.setGeometry(timetreeMap.getGeometryByCount(delayCount));
        else
          geoEvent.setGeometry(timetreeMap.getGeometryByTime(delayMilliseconds));

        if (geoEvent.getGeometry() != null)
          result = geoEvent;
      }
      else
      {
        LOGGER.debug("Can't create new GeoEvent with line geometry, only one point in the history: ", geoEvent);
      }
    }
    return result;
  }

  @Override
  public void afterPropertiesSet()
  {
    try
    {
      isDelayCount = Boolean.parseBoolean(this.properties.get(IS_DELAY_COUNT).getValueAsString());

      delayCount = Short.parseShort(this.properties.get(DELAY_VALUE).getValueAsString());

      delayValue = Long.parseLong(this.properties.get(DELAY_VALUE).getValueAsString());

      delayValueUnit = TimeUnit.valueOf(this.properties.get(DELAY_VALUE_UNITS).getValueAsString());

      delayField = this.properties.get(DELAY_FIELD).getValueAsString();

      if (LOGGER.isTraceEnabled())
      {
        if (isDelayCount)
          LOGGER.trace("Is Using Event Count Window: {0}, Event Count Window: {1}", isDelayCount, delayCount, delayValue, delayValueUnit, delayField);
        else
          LOGGER.trace("Is Using Event Count Window: {0}, Event Time Window: {4} {2} {3}", isDelayCount, delayCount, delayValue, delayValueUnit, delayField);
      }
    }
    catch (Exception ex)
    {
      LOGGER.error("Failed to get processor properties.", ex);
    }
    delayMilliseconds = delayValueUnit.toMillis(delayValue);
  }

  @Override
  public void validate() throws ValidationException
  {
    Property clearCacheProperty = this.properties.get(CLEAR_CACHE);
    boolean clearCache = Boolean.parseBoolean(clearCacheProperty.getValueAsString());

    if (clearCache)
    {
      geoEventTimeKeySet.clear();
      LOGGER.debug("Clear cache requested, cleared key caches.");
    }
    getProperty(CLEAR_CACHE).setValue(false);

    clearCacheProperty = this.properties.get(CLEAR_CACHE);
    clearCache = Boolean.parseBoolean(clearCacheProperty.getValueAsString());

    LOGGER.trace("Clear Cache: {0}", clearCache);

    super.validate();
  }

  @Override
  public void send(GeoEvent geoEvent) throws MessagingException
  {
    if (geoEventProducer != null && geoEvent != null)
    {
      geoEvent.setProperty(GeoEventPropertyName.TYPE, "event");
      geoEvent.setProperty(GeoEventPropertyName.OWNER_ID, getId());
      geoEvent.setProperty(GeoEventPropertyName.OWNER_URI, definition.getUri());
      geoEventProducer.send(geoEvent);
      LOGGER.debug("Sent GeoEvent to consumers: {0}", geoEvent);
    }
  }

  public void setMessaging(Messaging messaging)
  {
    this.messaging = messaging;
    this.geoEventCreator = messaging.createGeoEventCreator();
  }

  @Override
  public String getStatusDetails()
  {
    return (geoEventProducer != null) ? geoEventProducer.getStatusDetails() : "";
  }

  @Override
  public boolean isGeoEventMutator()
  {
    return true;
  }

  @Override
  public EventDestination getEventDestination()
  {
    return (geoEventProducer != null) ? geoEventProducer.getEventDestination() : null;
  }

  @Override
  public List<EventDestination> getEventDestinations()
  {
    return (geoEventProducer != null) ? Collections.singletonList(geoEventProducer.getEventDestination()) : new ArrayList<>();
  }

  @Override
  public void disconnect()
  {
    if (geoEventProducer != null)
      geoEventProducer.disconnect();
  }

  @Override
  public boolean isConnected()
  {
    return (geoEventProducer != null) && geoEventProducer.isConnected();
  }

  @Override
  public void setup() throws MessagingException
  {
    ;
  }

  @Override
  public void init() throws MessagingException
  {
    ;
  }

  @Override
  public void update(Observable o, Object arg)
  {
    ;
  }

  @Override
  public void setId(String id)
  {
    super.setId(id);
    geoEventProducer = messaging.createGeoEventProducer(new EventDestination(id + ":event"));
  }
}
