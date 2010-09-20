package com.ldodds.slug;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.net.*;

import com.ldodds.slug.framework.*;
import com.ldodds.slug.framework.config.Component;
import com.ldodds.slug.framework.config.ComponentFactory;
import com.ldodds.slug.framework.config.Memory;
import com.ldodds.slug.framework.config.MemoryFactory;
import com.ldodds.slug.http.*;
import com.ldodds.slug.vocabulary.CONFIG;
import com.ldodds.slug.vocabulary.SCUTTERVOCAB;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author ldodds
 *
 */
public class Scutter implements Runnable
{
  private static final String DEFAULT_USER_AGENT = "Slug Semantic Web Crawler (http://www.ldodds.com/projects/slug)";
  
  private MemoryFactory memoryFactory;
  private Memory memory;
  private FilteringController controller;
  private int numberOfWorkers;
  private String configFile;
  private String scutterId;
  private String scutterPlan;
  private boolean freshen;
  
  private static Logger logger = Logger.getLogger(Scutter.class.getPackage().getName());
  
  public Scutter() throws Exception
  {
    numberOfWorkers = 5;
    scutterId = "default";
    freshen = false;
  }
  
	/**
	 * Retrieve all rdfs:seeAlso links from a Model, and add each link to 
	 * the provided set as a URLTask.
	 * 
	 * @param model the model to scan
	 * @param set the Set to which URLs will be added
	 * @return the provided set
	 */
	public Set<URLTask> getSeeAlsoAsTasks(Model model, Set<URLTask> set)
	{
		NodeIterator iter = model.listObjectsOfProperty(RDFS.seeAlso);
		while (iter.hasNext())
		{
			RDFNode node = iter.nextNode();
			String seeAlso = node.toString(); 
			try
			{
				set.add( new URLTaskImpl( new URL(seeAlso) ) );
			} catch (MalformedURLException e)
			{
				logger.log(Level.WARNING, "Unable to build URL", e);				
			}
					
		}
		return set;				
	}  
	
  /**
     * @param scutterplan
     * @param urls
     * @throws FileNotFoundException
     * @throws MalformedURLException
     */
  public void setScutterPlan(String scutterplan) throws FileNotFoundException, MalformedURLException {
	logger.config("Scutter plan: " + scutterplan);
	this.scutterPlan = scutterplan;	
  }

  private List<Task> readScutterPlan() throws FileNotFoundException, MalformedURLException {
	    Set<URLTask> tasks = new HashSet<URLTask>();
        
	    Model plan = ModelFactory.createDefaultModel();
	    logger.finer("Reading scutter plan");
	    //FIXME other formats    
	    FileManager.get().readModel(plan, scutterPlan, "TURTLE");
	    
	    //get all seeAlso links from scutter plan, and add to work plan
	    getSeeAlsoAsTasks(plan, tasks);
	    	    
	    List<Task> urls = new ArrayList<Task>( tasks.size() );
	    urls.addAll(tasks);
	    return urls;	  
  }
  
  public void run() {
    try {
      Model config = readConfig();
      Resource me = getSelf(config);
      
      //create and load memory of previous crawls
      memoryFactory = new MemoryFactory(me);
      memory = memoryFactory.getMemory();
      logger.finer("Crawler memory type:" + memory.getClass().getName() );
      memory.load();
      logger.info("Crawler memory loaded");
      
      //create the worker factory 
      URLRetrievalWorkerFactory factory = new URLRetrievalWorkerFactory();
      factory.setScutter(me);
      
      //configure the shared memory
      factory.setMemoryFactory( memoryFactory );      
      
      //what to do with fetched data      
      ComponentFactory componentFactory = new ComponentFactory();
      List<Component> consumers = componentFactory.instantiateComponentsFor(me, CONFIG.consumers);         
      DelegatingConsumerImpl consumer = new DelegatingConsumerImpl(consumers);      
      
      //wire everything up
      consumer.setMemory(memory);      
      factory.setConsumer( consumer );

      logger.finer("Crawler components configured");
      
      //how to monitor progress, TODO make this configurable
      Monitor monitor = new MonitorImpl();
      factory.setMonitor( monitor );
                
      //be polite and set JVM user agent
      setUserAgent(me);
            
      //how many workers?
      numberOfWorkers = me.getProperty(CONFIG.workers).getInt();
      
      controller = new FilteringController(factory , numberOfWorkers, monitor);

      controller.addWorkItems( readScutterPlan(), "scutterplan");
      
      if (freshen) {
          controller.addWorkItems( readURLsFromMemory(), "memory" );       
      }

      
      //how to filter tasks
      List<Component> filters = componentFactory.instantiateComponentsFor(me, CONFIG.filters);
      DelegatingTaskFilterImpl filter = new DelegatingTaskFilterImpl(filters);      
      controller.addFilter( filter );
          
      controller.run();
    } catch (Exception e)
    {
      //HACK!
      throw new RuntimeException(e);
    }
  }
  
  public void setUserAgent(Resource me) {
	  String userAgent = DEFAULT_USER_AGENT;
	  if ( me.hasProperty(CONFIG.userAgent) ) {
		  userAgent = me.getProperty(CONFIG.userAgent).getObject().toString();
	  }
	  logger.config("User agent: " + userAgent);
      System.setProperty("http.agent", userAgent);	
  }

 private List<Task> readURLsFromMemory() throws MalformedURLException
  {
	logger.finer("Reading urls from crawler memory");
    List<Task> urls = new ArrayList<Task>();
    //loop through existing memory and add all previously found urls to work plan
    ResIterator reps = memory.getAllRepresentations();
    if (reps == null) {
    	return urls;
    }
    while (reps.hasNext())
    {
      Resource representation = reps.nextResource();
      if (!representation.hasProperty(SCUTTERVOCAB.skip)
        && representation.hasProperty(SCUTTERVOCAB.source))
      {
        RDFNode node = representation.getProperty(SCUTTERVOCAB.source).getObject();
        urls.add( new URLTaskImpl( new URL( node.toString() ) ) );
      }
    }
    return urls;
  }
  
  private Model readConfig() throws FileNotFoundException
  {
    Model config = ModelFactory.createDefaultModel();
    config.read( new FileInputStream(configFile), "");
    
    Model schema = ModelFactory.createDefaultModel();
    schema.read( this.getClass().getResourceAsStream("/config.rdfs"), "");
    
    return ModelFactory.createRDFSModel(schema, config);
  }
  
  private Resource getSelf(Model config)
  {
    return config.getResource(scutterId);
  }
  
  public void stop()
  {
	logger.info("Stopping scutter");
    if (controller != null)
    {
        controller.stop();
    }
  }
  
  public void save() throws Exception
  {
    logger.info("Saving memory");
    memory.save();
  }
  
  public void setConfig(String config) 
  {
	logger.config("Using scutter config: " + config);
    configFile = config;
  }
  
  public void setId(String id)
  {
	logger.config("Scutter config id: " + id);
    scutterId = id;
  }
  
  public void setFreshen(boolean freshen)
  {
	if (freshen) {
		logger.config("Freshening urls in memory");
	}
    this.freshen = freshen;
  }
  
  /**
   * Run the scutter from the command-line.
   * 
   * Arguments are:
   * 
   * -config : RDF file to use as Scutters config
   * -id : name of scutter, as identified in config
   * -plan : initial scutter plan
   */
    public static void main(String[] args) throws Exception
    {      
      final Scutter scutter = new Scutter();
      logger.config("Configuring Scutter");     
      configure(scutter, args );
      
      logger.config("Registering shutdown hook");
    
      Runtime.getRuntime().addShutdownHook(new Thread()
            {
            public void run()
            {
                logger.log(Level.INFO, "Running shutdown hook");
                scutter.stop();
                try
                {
                    scutter.save();
                } catch (Exception e)
                {
                	logger.log(Level.SEVERE, "Error saving Scutter state", e);
                    e.printStackTrace();
                }
            }
            });
    
      logger.info("Starting Scutter");
      Thread scutterThread = new Thread(scutter);
      scutterThread.setDaemon(false);
      scutterThread.start();
      logger.info("Scutter Thread Started");
    }
    
    private static void configure(Scutter scutter, String[] args) throws Exception
    {
        if (args.length < 1) {
          printHelp();  
          return;
        }

        try {
	        for (int i=0; i<args.length; i++)
	        {
	            String arg = args[i];
	            if (arg.equals("-config"))
	            {
	                scutter.setConfig( args[++i] );
	            }
	            else if ( arg.equals("-id") )
	            {
	                scutter.setId( args[++i] );             
	            }
	            else if (arg.equals("-plan"))
	            {
	                scutter.setScutterPlan(args[++i]);
	            }
	            else if (arg.equals("-freshen"))
	            {
	              scutter.setFreshen(true);
	              ++i;
	            }
	            else
	            {
	                System.err.println("Unknown argument '" + arg + "' ignored");
	            }
	        }
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Unexpected error", e);
        }
    }

    private static void printHelp() {
      System.out.println("Expected parameters");
      System.out.println("  -config <filename>");
      System.out.println("  -id <scutter id>");
      System.out.println("  -plan <plan uri>");
      System.out.println("  -freshen (optional)");
      System.exit(0);
    }
    
}
