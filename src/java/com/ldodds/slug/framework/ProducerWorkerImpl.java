package com.ldodds.slug.framework;


/**
 * @author ldodds
 */
public abstract class ProducerWorkerImpl extends WorkerImpl implements Producer
{
    private Consumer _consumer;
    private Task _workItem;
    
    public ProducerWorkerImpl(String name)
    {
        super(name);
    }
    
    
	/**
	 * @see com.ldodds.slug.framework.Producer#addConsumer(com.ldodds.slug.framework.Consumer)
	 */
	public void setConsumer(Consumer consumer)
	{
		_consumer = consumer;
	}

    protected abstract Result doTask(Task workItem);
    
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		_logger.finest( getName() + " running");
		
		while (true)
		{
	        //_workItem = _controller.popWorkItem(this);;
			_workItem = null;
			
	        while (_workItem == null )
	        {
	            try
	            {
	            	//_logger.finest( getName() + " waiting for task");
	            	Thread.sleep(500);
	            	if (_shouldStop)
	            	{
	            		break;          
	            	}	            	
	            } catch (InterruptedException ie)
	            {
	                //try again
	            }
		        _workItem = _controller.popWorkItem(this);		        
	        }
	        
	        //left the above loop either because there's something
	        //to do, or we should stop
	        if (_workItem != null)
	        {
				Result result = doTask(_workItem);
				//TODO capture no-ops and failures
				if (_consumer != null && result != null )
				{
					if ( result.isSuccess() && !result.isNoOp() ) {
						_logger.finest("Consuming results of successful task: " + _workItem.getId());
						_consumer.consume(_workItem, result);
					}
				}
				//TODO note error?
				_controller.completedTask(this, _workItem, result);
	        }
	        if (_shouldStop)
	        {
	        	break;
	        }
		}        
	}
}
