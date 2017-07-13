# Atlas Sample Project

![build status](https://circleci.com/gh/sts-atlas/demo2/tree/master.svg?style=shield&circle-token=41edafae9ba7a64b544cdbc352c5f3c9ff99f78d)

Follow the instructions below to create a new repository having full CI/CD into the Atlas cluster.  Changes checked into master (with successful tests run) will automatically be pushed into production.  Changes on branches will be built and tested, but not deployed.

## Table Of Contents
* [Prerequisites](#prerequisites)
* [Repository Setup](#repository-setup)
* [Local Swarm Setup](#local-swarm-setup)
* [Local Deployer API](#local-deployer-api)
* [Production Deployer API](#production-deployer-api)
* [Docker Compose Yaml](#docker-compose-yaml)
* [Logs](#logs)
* [Bash Scripts](#bash-scripts)

## Prerequisites
* Install [Docker](https://www.docker.com/community-edition#download)
* Be in the sts-atlas org to access [releases](https://github.com/sts-atlas/template/releases/latest) from the demo2 project

## Repository Setup

1.  Create and clone a [github repository](https://help.github.com/articles/creating-a-new-repository/) that you will host your project in
1.  Download the [latest release](https://github.com/sts-atlas/template/releases/latest) of the demo2 repository
1.  Unzip the contents of the release into your repository
    * cd into your new repository directory
    * Run the following command, replacing the path and version appropriately
        ```bash
        unzip /PATH/TO/YOUR/ZIP/demo2-<VERSION>.zip && cp -r atlas-samples-<VERSION>/ . && rm -Rf ./atlas-samples-<VERSION>
        ```
    * You now have a repository that is the same as the demo2 repository
1.  Run the `bin/init` script in the project.
    * use only lower case org and project names
    * this does NOT need to match your github org or anything else
    * this will determine the namespace for your projects and services
    * these values will also be used for your service DNS
1.  Commit & Push
    ```bash
    git add .
    git commit -m "Adding files from sample project"
    git push
    ```
1.  Log into [circleci.com](https://circleci.com) (or create an account with your github user)
1.  In Circle, go to settings, projects, then find your new repo.  Select the gear on the right and then the environment variables menu item on the left. Add [environment variables](https://circleci.com/docs/2.0/env-vars/) for:
    1.  JWT_SECRET `16D37EB64AC84CE3897BC68F6A269A5A`
    1.  DOCKER_USERNAME `stsatlas+atlasquayrobot`
    1.  DOCKER_PASSWORD `HD449TCAIUROKR4CXMVJP10XQ82FA4H6NBTCMXG6ND8JJWZH88CMWAGAH051VKOT`
    > The DOCKER_USERNAME & DOCKER_PASSWORD are the login for your image repository
1.  Still in project settings, go to overview and select "Follow Project" to kick off your first build!
1.  If your first build fails with "job build not found", make a nonsense commit (a change that has no effect, like a new line) and push it! (This is a known bug in Circle, but is fixed with another push)
1. Once your Circle build passes, your app should be up in production, it can be accessed with \<service\>.\<org\>_\<project\>.swarm.commonstack.io

## Local Swarm Setup
* Run `bin/create-deployer`, which will init your swarm and start up a deployer
* Your swarm is now up!  Please refer to the local deployer api section of this readme to understand how to use a local deployer

## Local Deployer API

### With bin/atlas

* bin/atlas local-dockerize
  * This will build docker images locally for your services and put them in your local registry
* bin/atlas local-deploy
  * This will take your images from your local registry and push them into your local swarm
* bin/atlas local-poll
  * Use this to see the status of tasks running on your local swarm

### Using HTTP

* Deploy: POST /org/\<org\>/project/\<project\>
  * Along with the request you must send a file (using a multipart form).  One way to do this is with the -F flag on a curl, like so:
  ```bash
  curl -X POST --header "Authorization: Token $JWT" -F "file=@docker-compose.yml" -H "Host: deployer.sts-atlas_atlas-deploy.swarm.commonstack.io" http://localhost/org/<org>/project/<proejct>
  ```
  * This will deploy your services as defined in your docker compose file
* Poll: GET /org/\<org\>/project/\<project\>
  * This endpoint will return the status of the tasks running for your project (a task is a running service)
* Delete: DELETE /org/\<org\>/project/\<project\>
  * This endpoint will delete your stack

#### Proxy Service
Along with the deployer service, a proxy service is included in the deployer docker compose file.  The proxy allows us to forward requests such that requests to a subdomain actually get routed to a service.  So instead of using the random ports that docker assigns to its containers, you can use subdomains like my_service.org_project.swarm.commonstack.io.

#### Host Header
However, while developing locally all of the services run on localhost, so how do we connect to a specific service?  The answer is with a host header.  This host header tells the proxy service where to forward requests.  So to access a service called my_service, you make a request to localhost with a host header equal to what your production url would be:
```bash
curl -H 'Host:my_service.org_project.swarm.commonstack.io' localhost/metrics/healthcheck
```
Since the deployer is running on the swarm like any other service, you hit it with a host header like so:
```bash
curl -H 'Host:deployer.sts-atlas_atlas-deploy.swarm.commonstack.io' localhost/metrics/healthcheck
```

#### Authorization Header
Along with the host header, you just specify an Authorization header.  This is a JWT built with your specific org and project in the payload.  Instead of building this yourself, you should use the script in bin/jwtgen that we have already made for you.  For now we have a single jwt secret, which you must export as an environment variable.
```bash
export JWT_SECRET=16D37EB64AC84CE3897BC68F6A269A5A
```

## Production Deployer API
The production deployer follows the same API as the local one, but is hosted at deployer.sts-atlas_atlas-deploy.swarm.commonstack.io instead of localhost.

### With bin/atlas

* bin/atlas dockerize
  * This will build docker images for your services and push them to quay, a docker image registry
* bin/atlas deploy
  * This will take your images from the quay registry and push them into the production swarm
* bin/atlas poll
  * Use this to see the status of tasks running on the production swarm

### With HTTP

The API is same as above, but you do not need a host header, you still need an Authorization header though.

## Docker Compose Yaml
Read up on [Docker's own compose file docs](https://docs.docker.com/compose/compose-file/)

### Publish Environment Variable
You can specify an environment variable in your service to specify if a service is publically facing.  Add the `PUBLISH=true` environment variable to each service you want to be accessible from the outside internet.  If you do not specify `PUBLISH=true`, your app will still be deployed and your other services can still access it internal DNS (e.g. using http://otherservice), but it won't be publically availible on the internet.

### Service Ports Specification
In order to specify which port will be accessible from a published service you must specify the ports using the SERVICE_PORTS environment variable. By default, SERVICE_PORTS will be set to 4000 if not defined. If supplied, specified ports will be used (e.g. SERVICE_PORTS=8080,8081)
We disallow specifying the `ports` section in docker compose files.

## Logs

### Local
To view logs for a service running in your local swarm, you should use the docker cli.

1. Find the id of your service in the local swarm with `docker service ls`.  Your service will be named \<org\>_\<project\>-\<service\>
1. Call `docker service logs <serviceId>` replacing <serviceId> with the id that you found in the last step.

### Production
The only way to view logs of your production service is through [logmatic.io](logmatic.io).  If you were not given access to logmatic, please notify someone on the atlas team via [flowdock](https://www.flowdock.com/app/ca-technologies/sts-9) (on the sts-9 flow in the ca-technologies organization) and we will give you access so you can view logs for your service.

## Bash Scripts
Instead of building a complex CLI, we decided to instead use bash scripts that are versioned right along with your code.  You can find these in the bin folder.  This solution eliminates a lot of worry about versioning and distributing a CLI, and allows the developers of a service to customize their CLI experience easily.  You can make your own commands, or modify existing commands to suit your needs.  Here is a general overview of the default bin scripts that we provide.

### bin/atlas
This is the one stop shop for everything you need to do with your service.  It orchestrates project level commands (like deploy, poll, dockerize)  and service level commands (like build, test, install).  Project level commands are defined right in the bin/atlas file, and are simply curls to the deployer service.  For service level commands, the bin/atlas script defers control to a bash script that lives in your service directory (i.e. clojure-lein/bin/service).

### bin/create-deployer
As described above, this script starts a deployer locally using the deployer-compose.yml and the load-balancer.yml.

### bin/init
This script is specific to the demo2 repo.  Its purpose is to initialize your project with correct org and project names.  It asks you for your org and project name, then modifies files (including bin/atlas) to use those names.  We needed this because the way we identify a service is with a concatenation of org, project, and service names.  If everyone was using org sts-atlas with project atlas-samples and service names clojure-lein and java-spring-boot, people would be trampling over other people's services.
