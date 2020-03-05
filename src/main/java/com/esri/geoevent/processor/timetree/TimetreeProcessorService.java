package com.esri.geoevent.processor.timetree;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;

public class TimetreeProcessorService extends GeoEventProcessorServiceBase
{
  private Messaging messaging;

  public TimetreeProcessorService()
  {
    this.definition = new TimetreeProcessorDefinition();
  }

  @Override
  public GeoEventProcessor create() throws ComponentException
  {
    TimetreeProcessor processor = new TimetreeProcessor(definition);
    processor.setMessaging(messaging);
    return processor;
  }

  public void setMessaging(Messaging messaging)
  {
    this.messaging = messaging;
  }
}
