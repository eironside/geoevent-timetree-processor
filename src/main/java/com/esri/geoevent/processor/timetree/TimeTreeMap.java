package com.esri.geoevent.processor.timetree;

import java.util.Map;
import java.util.TreeMap;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;

public class TimeTreeMap extends TreeMap<Long, TimetreeGeoEvent>
{
  private static final long         serialVersionUID = 5357525077241680115L;
  private static final BundleLogger LOGGER           = BundleLoggerFactory.getLogger(TimetreeProcessor.class);

  private short                     maxSize          = -1;
  private long                      maxAge           = -1;
  private SpatialReference          spatialReference = null;

  // public TimeTreeMap()
  // {
  // super();
  // }

  public TimeTreeMap(short maxSize)
  {
    super();
    this.maxSize = maxSize;
  }

  public TimeTreeMap(long maxAge)
  {
    super();
    this.maxAge = maxAge;
  }

  @Override
  public TimetreeGeoEvent put(Long key, TimetreeGeoEvent value)
  {
    TimetreeGeoEvent result = null;
    if (value != null && value.getGeoEvent() != null && value.getGeoEvent().getGeometry() != null && Geometry.Type.Point.equals(value.getGeoEvent().getGeometry().getGeometry().getType()))
    {
      LOGGER.trace("Adding to tree[{2}]: {0},{1}", key, value, this.keySet().size());
      result = super.put(key, value);
      if (spatialReference == null)
      {
        this.spatialReference = value.getGeoEvent().getGeometry().getSpatialReference();
        LOGGER.trace("Set spatial reference: {0}", this.spatialReference);
      }

      LOGGER.trace("Checking location keys.");

      Map.Entry<Long, TimetreeGeoEvent> curEntry = this.lastEntry();
      while (curEntry != null && curEntry.getValue() != null)
      {
        boolean removed = false;
        String curLocationKey = curEntry.getValue().getLocationKey();
        LOGGER.trace("\tChecking location keys. CUR_KEY: {0}[{1}]", curEntry.getKey(), curLocationKey);
        Map.Entry<Long, TimetreeGeoEvent> preEntry = this.lowerEntry(curEntry.getKey());
        if (preEntry != null && preEntry.getValue() != null)
        {
          String preLocationKey = preEntry.getValue().getLocationKey();
          LOGGER.trace("\tChecking location keys. PRE_KEY: {0}[{1}]", preEntry.getKey(), preLocationKey);
          if (preLocationKey != null && preLocationKey.equals(curLocationKey))
          {
            LOGGER.trace("\tKeys match, removing entry key: {0}", preEntry.getKey());
            removed = true;
            this.remove(preEntry.getKey());
          }
        }
        if (!removed)
          curEntry = this.lowerEntry(curEntry.getKey());
      }
    }
    else
    {
      LOGGER.debug("Cant add event to cache, geometry is wrong type: {0}", value);
    }

    Long firstKey = this.firstKey();
    if (maxSize > 0)
    {
      if (this.size() > maxSize)
      {
        LOGGER.trace("Removing event from cache, size reached limit: {0}", firstKey);
        this.remove(firstKey);
      }
    }
    else
    {
      while (((System.currentTimeMillis() - firstKey) > maxAge))
      {
        LOGGER.trace("Removing event from cache, time limit reached limit: {0}", firstKey);
        this.remove(firstKey);
        firstKey = this.firstKey();
      }
    }

    return result;
  }

  public MapGeometry getGeometryByCount(long count)
  {
    LOGGER.trace("Creating new geometry for count {0}.", count);
    return getGeometry(System.currentTimeMillis(), count);
  }

  public MapGeometry getGeometryByTime(long rangeMiillis)
  {
    LOGGER.trace("Creating new geometry for time range {0} ms.", rangeMiillis);
    return getGeometry(rangeMiillis, this.keySet().size() + 1);
  }

  private MapGeometry getGeometry(long rangeMiillis, long count)
  {
    MapGeometry result = null;
    if (this.keySet().size() > 0)
    {

      try
      {
        final Polyline polyline = new Polyline();

        long startTime = System.currentTimeMillis() - rangeMiillis;

        this.forEach((time, treeGeoevent) ->
          {
            if (treeGeoevent.getTime() >= startTime && polyline.getSegmentCount() <= count)
            {
              Point geoPoint = ((Point) treeGeoevent.getGeoEvent().getGeometry().getGeometry());
              if (polyline.isEmpty())
              {
                LOGGER.trace("Creating new geometry starting at point: ", geoPoint);
                polyline.startPath(geoPoint.getX(), geoPoint.getY());
              }
              else
              {
                LOGGER.trace("Adding point to geometry: ", geoPoint);
                polyline.lineTo(geoPoint.getX(), geoPoint.getY());
              }
            }
          });
        if (!polyline.isEmpty())
        {
          result = new MapGeometry(polyline, this.spatialReference);
          LOGGER.trace("Returning new map geometry: ", result);
        }
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Failed to create geometry", e);
        else
          LOGGER.info("Failed to create geometry", e.getMessage());
      }
    }
    return result;
  }

  public short getMaxSize()
  {
    return maxSize;
  }

  public void setMaxSize(short maxSize)
  {
    this.maxSize = maxSize;
  }
}
