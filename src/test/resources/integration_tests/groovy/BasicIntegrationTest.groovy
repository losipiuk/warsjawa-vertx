/**
 * Simple integration test which shows tests deploying other verticles, using the Vert.x API etc
 */


import com.kgslo.myproject.viano.integration.groovy.GroovySomeVerticle

import static org.vertx.testtools.VertxAssert.*

// And import static the VertxTests script
import org.vertx.groovy.testtools.VertxTests

// The viano methods must being with "viano"

// This demonstrates using the Vert.x API from within a viano.
def testHTTP() {
  // Create an HTTP server which just sends back OK response immediately
  vertx.createHttpServer().requestHandler({ req ->
    req.response.end()
  }).listen(8181, { asyncResult ->
    assertTrue(asyncResult.succeeded)
    // The server is listening so send an HTTP request
    vertx.createHttpClient().setPort(8181).getNow("/", { resp ->
      assertEquals(200, resp.statusCode)
      /*
      If we get here, the viano is complete
      You must always call `testComplete()` at the end. Remember that testing is *asynchronous* so
      we cannot assume the viano is complete by the time the viano method has finished executing like
      in standard synchronous tests
      */
      testComplete()
    })
  })
}

/*
  This viano deploys some arbitrary verticle - note that the call to testComplete() is inside the Verticle `GroovySomeVerticle`
  GroovySomeVerticle is a viano verticle written in Groovy
*/
def testDeployArbitraryVerticle() {
  assertEquals("bar", "bar")
  container.deployVerticle("groovy:" + GroovySomeVerticle.class.getName())
}

def testCompleteOnTimer() {
  vertx.setTimer(1000, { timerID ->
    assertNotNull(timerID)

    // This demonstrates how tests are asynchronous - the timer does not fire until 1 second later -
    // which is almost certainly after the viano method has completed.
    testComplete()
  })
}

VertxTests.initialize(this)
VertxTests.startTests(this)


