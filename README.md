#Requirements


#### *ElasticSearch*
Application leverages an *ElasticSearch* instance to perform multi-column wildcard look-ups in its repository.
Make sure you have elasticsearch running on port 9200 before running application.

Simple setup via *Docker*: 
1. Get an ElasticSearch docker image if you don't have one.
```docker pull docker.elastic.co/elasticsearch/elasticsearch:7.10.0```  
2. Run it using Docker on port 9200.
```docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.10.0```  