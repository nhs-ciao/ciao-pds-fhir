Overview
========

This is the first proof of concept CIP for the CIAO project.

The objective of the CIP is to provide a minimal implementation of a ReST-based FHIR server, supporting just one FHIR resource (Patient), which acts as a facade in front of the national Personal Demographics Service (PDS). This would effectively allow a client to query PDS using a simple FHIR request.

The scope of this trial CIP is limited to a PDS simple trace interaction, so it will not support all aspects of the FHIR standard, nor will it provide all functionality available in PDS (in fact is is a subset of the functionality outlined in the PDS mini services specification).

Scope / Progress
----------------

NOTE: This is a limited proof-of-concept CIP. As part of the ongoing development of CIAO, it may be desiable to develop a full PDS FHIR CIP. The discussion around the design of such a CIP will be held in the [CIAO documentation repository](https://github.com/nhs-ciao/ciao-design/tree/master/CIP%20Design)

Specific functionality for this proof-of-concept CIP:

* Accept GET requests for a patient resource with basic FHIR query parameters (e.g. http://localhost:8080/fhir/Patient?family=SMITH) **complete**
* Parse the request and build a Spine simple trace message **complete**
* Send the request to a running instance of the Spine TKW simulator **complete**
* Send the request to a Spine test region over a TLS MA link **not started**
* Parse the response **partially complete**
* Populate a FHIR patient resource and return it to the requestor **in progress**
* Error handling **not started**

Requirement Traceability
------------------------

Some requirements from published specifications have been used to ensure this CIP roughly aligns with current guidance. Where possible, these are referenced directly as comments in the code. The below naming convention has been used to identify requirements:

**REQ-SRC-NNN**

**SRC** = Source - one of:

* PDSMS = ITK Spine Mini Services - PDS Provider Requirements (part of the "ITK Spine Mini Service" bundle on TRUD).

**NNN** = Requirement Number. Where a source does not number its requirements in this way, a document section number will be used instead.

So, a comment mentioning REQ-PDSMS-4.5.2 would refer to the requirement(s) outlined in section 4.5.2 of the ITK Spine Mini Services - PDS Provider Requirements document.