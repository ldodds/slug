package com.ldodds.slug.framework;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.http.URLTaskImpl;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class ControllerTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		Controller controller = getMockedController();
		assertEquals(10, controller.getNumberOfWorkers());
		assertEquals(0, controller.getQueueSize() );
		assertNull(controller.getStarted() );
		
	}
	
	public void testPopWorkItemWithEmptyList() throws Exception {
		Controller controller = getMockedController();
		Task task = controller.popWorkItem(null);
		assertNull(task);
	}

	public void testAddWorkItem() throws Exception {
		Controller controller = getMockedController();
		URLTask task = new URLTaskImpl(new URL("http://www.example.org"));
		boolean success = controller.addWorkItem(task);
		assertTrue(success);
		assertEquals(1, controller.getQueueSize() );		
	}

	public void testAddWorkItemWithDuplicate() throws Exception {
		Controller controller = getMockedController();
		URLTask task = new URLTaskImpl(new URL("http://www.example.org"));
		boolean success = controller.addWorkItem(task);
		assertTrue(success);
		assertEquals(1, controller.getQueueSize() );
		success = controller.addWorkItem(task);
		assertTrue(!success);
		assertEquals(1, controller.getQueueSize() );
	}
	
	public void testAddMultipleItems() throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add( new URLTaskImpl(new URL("http://www.example.org")) );
		tasks.add( new URLTaskImpl(new URL("http://www.example.com")) );
		Controller controller = getMockedController();
		int added = controller.addWorkItems(tasks, "test");
		assertEquals(2, added);
		assertEquals(2, controller.getQueueSize() );
	}

	public void testAddMultipleItemsWithDuplicates() throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add( new URLTaskImpl(new URL("http://www.example.org")) );
		tasks.add( new URLTaskImpl(new URL("http://www.example.org")) );
		Controller controller = getMockedController();
		int added = controller.addWorkItems(tasks, "test");
		assertEquals(1, added);
		assertEquals(1, controller.getQueueSize() );
	}
	
	public void testMonitorNotifiedWhenWorkItemIsPopped() throws Exception {
		URLTask task = new URLTaskImpl(new URL("http://www.example.org"));
		
		Monitor monitor = createMock(Monitor.class);
		monitor.setController((Controller)anyObject());
		monitor.startingTask(null, task);
		
		WorkerFactory factory = createMock(WorkerFactory.class);
		factory.setController( (Controller)anyObject() );
		factory.setMonitor( (Monitor)anyObject() );
		
		replay(factory);
		replay(monitor);
		
		Controller controller = new Controller(factory, 10, monitor);
		controller.addWorkItem(task);
		Task workitem = controller.popWorkItem(null);
		assertEquals(task, workitem);
		
		verify(monitor);
		verify(factory);		
	}

	public void testRun() throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add( new URLTaskImpl(new URL("http://www.example.org")) );
		tasks.add( new URLTaskImpl(new URL("http://www.example.com")) );
		
		Controller controller = getMockedControllerWithMonitorExpectingTasks();
		controller.addWorkItems(tasks, "test");
		controller.run();
	}
	
	private Controller getMockedControllerWithMonitorExpectingTasks() throws Exception {
		Monitor monitor = createMock(Monitor.class);
		makeThreadSafe(monitor, true);
		monitor.setController((Controller)anyObject());
		expectLastCall().anyTimes();
		monitor.startingTask((Worker)anyObject(), (Task)anyObject());
		expectLastCall().anyTimes();
		monitor.completedTask((Worker)anyObject(), (Task)anyObject(), (Result)anyObject());
		expectLastCall().anyTimes();

		expect( monitor.getNumberOfActiveWorkers() ).andReturn(0).anyTimes();
		
		replay(monitor);
		
		WorkerFactory factory = new TestWorkerFactory();
				
		Controller controller = new Controller(factory, 10, monitor);
		
		verify(monitor);
		return controller;		
	}
	
	private Controller getMockedController() throws Exception {
		Monitor monitor = createMock(Monitor.class);
		monitor.setController((Controller)anyObject());
		
		WorkerFactory factory = createMock(WorkerFactory.class);
		factory.setController( (Controller)anyObject() );
		factory.setMonitor( (Monitor)anyObject() );
		
		replay(factory);
		replay(monitor);
		
		Controller controller = new Controller(factory, 10, monitor);
		
		verify(monitor);
		verify(factory);
		return controller;
	}

	public static class TestWorkerFactory extends WorkerFactoryImpl {

		protected Worker createWorker(String name) {
			return new TestWorker( name, ResultImpl.noop() );
		}		
	}
	
	public static class TestWorker extends ProducerWorkerImpl {

		private Result result;
		
		public TestWorker(String name, Result result) {
			super(name);
			this.result = result;
		}
		
		protected Result doTask(Task workItem) {
			return result;
		}
		
	}
	
}
