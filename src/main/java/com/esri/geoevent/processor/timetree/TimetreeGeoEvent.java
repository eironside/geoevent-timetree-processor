package com.esri.geoevent.processor.timetree;

import com.esri.core.geometry.Geometry.Type;
import com.esri.core.geometry.Point;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;

public class TimetreeGeoEvent implements TimetreeProperties
{
  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(TimetreeProcessor.class);

  private final GeoEvent            geoEvent;
  private final Long                time;
  private final String              locationKey;
  private final String              trackKey;
  // private final String timeKey;

  public TimetreeGeoEvent(GeoEvent geoEvent, String timeField)
  {
    this.geoEvent = geoEvent;
    String definitionGuid = geoEvent.getGeoEventDefinition().getGuid();
    long keyTime = geoEvent.getReceivedTime().getTime();

    if (TIME_END.equals(timeField))
      keyTime = geoEvent.getEndTime().getTime();
    else if (TIME_START.equals(timeField))
      keyTime = geoEvent.getStartTime().getTime();

    this.time = keyTime; // key to TimeTreeMap
    this.trackKey = definitionGuid + "_" + geoEvent.getTrackId(); // key to LinkedHashMap
    // this.timeKey = definitionGuid + "_" + geoEvent.getTrackId() + "_" + keyTime;

    if (geoEvent.getGeometry() != null && geoEvent.getGeometry().getGeometry() != null && geoEvent.getGeometry().getGeometry().getType().equals(Type.Point))
    {
      // only point geometries are supported
      Point geometry = (Point) geoEvent.getGeometry().getGeometry();
      this.locationKey = geometry.getX() + "_" + geometry.getY();
    }
    else
    {
      this.locationKey = null;
    }

    LOGGER.trace("Created delayed event with timeKey {0} using {1} field time {2}. Location key is {4}. Release time is {3}.", time, timeField, keyTime, time, locationKey);
  }

  public String getLocationKey()
  {
    return this.locationKey;
  }

  public String getTrackKey()
  {
    return this.trackKey;
  }

  public GeoEvent getGeoEvent()
  {
    return this.geoEvent;
  }

  public Long getTime()
  {
    return time;
  }

  @Override
  public String toString()
  {
    return "TimetreeGeoEvent [time=" + time + ", locationKey=" + locationKey + ", trackKey=" + trackKey + ", geoEvent=" + geoEvent + "]";
  }

  // public String getTimeKey()
  // {
  // return this.timeKey;
  // }
  // @Override
  // public long getDelay(TimeUnit unit)
  // {
  // long diff = this.time - System.currentTimeMillis();
  // return unit.convert(diff, TimeUnit.MILLISECONDS);
  // }
  //
  // @Override
  // public int compareTo(Delayed obj)
  // {
  // int result = Long.compare(this.time, ((TimetreeGeoEvent) obj).time);
  // if (useTrackID)
  // {
  // result = this.timeKey.compareTo(((TimetreeGeoEvent) obj).timeKey);
  // }
  //
  // return result;
  // }
}
