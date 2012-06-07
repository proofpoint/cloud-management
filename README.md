Overview
========
Proofpoint Cloud Management (PCM) is a cloud-agnostic SaaS deployment tool that provides support for managed
software deployment. PCM provides a user with the capability to manage virtual machine instances within any
supported cloud environment, including public clouds such as Amazon Web Services. Furthermore, PCM can assemble
managed virtual machines into complex software systems by instantiating and applying application and system
configuration templates captured in a tool such as Puppet.

PCM does not perform any actions itself, it merely abstracts out the underlying cloud providers themselves.
Semantics about how operations are performed within a provider should be consulted, as actions can differ
between cloud providers.

API
===

Instance Collection Resource: /v1/instance
------------------------------------------

### GET
Retrieves a list of all known instances

#### Request

    http://localhost:8888/v1/instance

#### Response
The response will be 200 OK with the following body.

    [
     {
      "id" : "1234-foo",
      "provider" : "openstack",
      "location" : "sjc3",
      "name" : "mattstep-1234",
      "size" : "m1.medium",
      "status" : "ACTIVE",
      "hostname" : "foo-1234.lab.bar.com",
      "tags" : ["FooService","BarService"],
      "self" : "http://localhost:8888/v1/instance/1234-foo"
     },
     {
      "id" : "2345-bar",
      "provider" : "aws",
      "location" : "us-east-1a",
      "name" : "mattstep-2345",
      "size" : "c1.xlarge",
      "status" : "ACTIVE",
      "hostname" : "bar-2345.lab.bar.com",
      "tags" : ["FooService","BarService"],
      "self" : "http://localhost:8888/v1/instance/2345-bar"
     }
    ]

### POST
Creates a new instance in the underlying cloud provider.

#### Request
URI:

    http://localhost:8888/v1/instance

Body (All fields are required):

    {
     "size" : "t1.micro",
     "provider" : "aws",
     "location" : "us-west-1a",
     "namePrefix" : "mattstep"
    }

#### Response
Responses are bodyless with a 201 CREATED code and a Location header indicating where the created resource can be found later on.

    Location: http://localhost:8888/v1/instance/3456-baz

For failed requests, the response will contain a JSON body of the following format.

    {
     "requestedInstance" : {
      "size" : "t1.micro",
      "provider" : "aws",
      "location" : "us-west-1a",
      "namePrefix" : "mattstep"
     },
     "error" : "Capacity Unavailable"
    }

It can be difficult to code to all potential failures, but the following errors are known to potentially occur, all of which will have a 400 response code :

- Size Unavailable - The instance size requested is not supported by the provider at that location.
- Location Unavailable - The location is unavailable for that provider.
- Provider Unavailable - There is no provider available to support this request.

Instance Resource: /v1/instance/{instance-id}
---------------------------------------------

### GET

Retrieves an instance

#### Request

    http://localhost:8888/v1/instance/1234-foo

#### Response
The response will be 200 OK with the following body.

    {
     "id" : "1234-foo",
     "provider" : "openstack",
     "location" : "sjc3",
     "name" : "mattstep-1234",
     "size" : "m1.medium",
     "status" : "ACTIVE",
     "hostname" : "foo-1234.lab.bar.com",
     "tags" : ["FooService","BarService"],
     "self" : "http://localhost:8888/v1/instance/1234-foo"
    }


### DELETE
Terminates an instance

#### Request

    http://localhost:8888/v1/instance/1234-foo

#### Response
Response is bodyless with a 204 NO CONTENT code upon success.

Tag Resource: /v1/instance/{instance-id}/tag/{tag}
--------------------------------------------------

### PUT

Creates a tag for an instance

#### Request

    http://localhost:8888/v1/instance/1234-foo/tag/FooService

#### Response
Response is bodyless with a 204 NO CONTENT code upon success.


### DELETE

Removes a tag for an instance

#### Request

    http://localhost:8888/v1/instance/1234-foo/tag/FooService

#### Response
Response is bodyless with a 204 NO CONTENT code upon success.

Provider Collection Resource: /v1/provider
------------------------------------------

### GET
Retrieves a list of all registered cloud providers.

#### Request

    http://localhost:8888/v1/provider

#### Response
The response will be 200 OK with the following body.

    [
     {
      "provider" : "openstack",
      "name" : "OpenStack Nova",
      "self" : "http://localhost:8888/v1/provider/openstack"
     },
     {
      "provider" : "aws",
      "name" : "Amazon Web Services",
      "self" : "http://localhost:8888/v1/provider/aws"
     }
    ]

Provider Resource : /v1/provider/{provider}
-------------------------------------------

### GET
Retrieves details for a given cloud provider.

#### Request

    http://localhost:8888/v1/provider/aws

#### Response
The response will be 200 OK with the following body.

    {
     "provider" : "aws",
     "name" : "Amazon Web Services",
     "self" : "http://localhost:8888/v1/provider/aws"
     "availableLocations" : [
      {
       "location" : "us-east-1a",
       "name" : "US East 1a",
       "self" : "http://localhost:8888/v1/provider/aws/location/us-east-1a"
      },
      {
       "location" : "us-west-1a",
       "name" : "US West 1a",
       "self" : "http://localhost:8888/v1/provider/aws/location/us-west-1a"
      }
     ]
    }

Location Resource: /v1/provider/{provider}/location/{location}
--------------------------------------------------------------

### GET
Retrieves available sizes to use for a given location

#### Request

    http://localhost:8888/v1/provider/aws/location/us-east-1a

#### Response
The response will be 200 OK with the following body.

    {
     "location" : "us-east-1a",
     "name" : "US East 1a",
     "self" : "http://localhost:8888/v1/provider/aws/location/us-east-1a",
     "availableSizes" : [
      {
       "size" : "t1.micro",
       "cores" : 1,
       "memory" : "613 MB",
       "disk" : "30 GB"
      },
      {
       "size" : "m1.small",
       "cores" : 1,
       "memory" : "1.7 GB",
       "disk" : "160 GB"
      },
      {
       "size" : "m1.medium",
       "cores" : 1,
       "memory" : "3.75 GB",
       "disk" : "410 GB"
      },
      {
       "size" : "m1.large",
       "cores" : 2,
       "memory" : "7.5 GB",
       "disk" : "850 GB"
      },
      {
       "size" : "m1.xlarge",
       "cores" : 4,
       "memory" : "15 GB",
       "disk" : "1.69 TB"
      },
      {
       "size" : "cc2.8xlarge",
       "cores" : 16,
       "memory" : "60.5 GB",
       "disk" : "3.37 TB"
      }
     ]
    }



Configuration
=============

Provider Listing
----------------
A single configuration property is used to define the providers to allocate.

    cloud-management.providers=devstack,hpcloud,aws,softlayer,rackspace

Individual Providers
--------------------
Each provider is then allocated with a collection of configuration properties.

At this commit, this is only tested for openstack-nova (against Openstack Essex) and aws-ec2.

    cloud-management.devstack.api=openstack-nova
    cloud-management.devstack.name=DevStack edition of OpenStack
    cloud-management.devstack.location=http://192.168.56.2:5000
    cloud-management.devstack.user=admin:admin
    cloud-management.devstack.secret=admin
    cloud-management.devstack.default-image-id=005dbf05-9e1c-4773-9fbc-48f7eea99aca

The following properties are supported for each provider you specify.  All are required unless otherwise stated.

- api : The jclouds api or provider to use for this provider (see http://www.jclouds.org/documentation/reference/supported-providers).
- name : A meaningful name for this provider.
- location : A symantically required endpoint to connect to this provider with.  For some jclouds providers this is not required.  Refer to http://jclouds.org for more information on which cloud providers and apis require this to be specified.
- user : The user to connect to the specified provider with.
- secret : The password or shared secret to use when connecting with the provider.
- default-image-id : The default image to be used when starting instances.
- aws-vpc-subnet-id : An optionally specified property for ec2 providers that will specify which vpc subnet to create new instances in.
