/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.nhs.itk.ciao.fhir;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

/**
 * Routes for our FHIR endpoint
 * 
 * Note: I think you will need the camel-jetty component in apache service mix:
 * features:install camel-jetty
 *
 * 
 * Interactions defined in the FHIR ReST API:
 * 
 * read			-> GET [base]/[type]/[id] {?_format=[mime-type]}
 * vread		-> GET [base]/[type]/[id]/_history/[vid] {?_format=[mime-type]}
 * update		-> PUT [base]/[type]/[id] {?_format=[mime-type]}
 * delete		-> DELETE [base]/[type]/[id]
 * create		-> POST [base]/[type] {?_format=[mime-type]}
 * search		-> GET [base]?[parameters] {&_format=[mime-type]}
 * validate		-> POST [base]/[type]/_validate{/[id]}
 * conformance	-> GET [base]/metadata {?_format=[mime-type]}
 *				   OPTIONS [base] {?_format=[mime-type]}
 * transaction	-> POST [base] {?_format=[mime-type]}
 * history		-> GET [base]/[type]/[id]/_history{?[parameters]&_format=[mime-type]}
 * 
 * Tag operations 
 * via header	-> Category: [Tag Term]; scheme="[Tag Scheme]"; label="[Tag label]"(, ...)
 * Get tags		-> GET [base]/_tags
 * 
 */
public class CIPRoutes extends RouteBuilder {

    public void configure() {
    	
    	// Put all new http requests for the patient resource onto a JMS queue
    	from("jetty:http://0.0.0.0:8080/fhir/Patient?traceEnabled=true").routeId("fhir-patient-http")
			.to("jms:ciao-fhir");
    	
    	// Read each request from the queue and route it to the appropriate bean to generate a response
    	from("jms:ciao-fhir").routeId("fhir-patient-requesthandler")
    		.choice()
            	.when(header(Exchange.HTTP_METHOD).isEqualTo("GET"))
            		.log("GET Request")
			        .beanRef("patientGetProcessor")
			    	.to("direct:spineSender")
            	.otherwise()
            		.log("OTHER Request")
            		.beanRef("patientPostProcessor");

    	// Send to Spine
    	from("direct:spineSender").routeId("fhir-patient-spineSender")
    		.wireTap("jms:ciao-spineRequestAudit")
    		// Actual URL is set in a request header prior to the below being called
    		.to("https://dummyurl?throwExceptionOnFailure=false")
    		.to("direct:responseProcessor");
    	
    	// Parse the response
    	from("direct:responseProcessor")
    		.wireTap("jms:ciao-spineResponseAudit")
    		.beanRef("patientResponseProcessor");
    	
    	// Log spine messages
    	from("jms:ciao-spineRequestAudit")
    		.to("file:log/spine?fileName=${date:now:yyyy-MM-dd}-${exchangeId}-Request.xml");
    	from("jms:ciao-spineResponseAudit")
    		.to("file:log/spine?fileName=${date:now:yyyy-MM-dd}-${exchangeId}-Response.xml");
    	
    	// Conformance Profile
    	from("jetty:http://0.0.0.0:8080/fhir/metadata?traceEnabled=true").routeId("fhir-conformance")
    		.to("direct:conformance");
    	
    	from("jetty:http://0.0.0.0:8080/fhir?traceEnabled=true").routeId("fhir-conformance-options")
			.choice()
            	.when(header(Exchange.HTTP_METHOD).isEqualTo("OPTIONS"))
			    	.to("direct:conformance");
    	
    	from("direct:conformance")
			.beanRef("conformanceProcessor");
    }
}