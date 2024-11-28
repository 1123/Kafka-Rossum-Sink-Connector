#!/bin/bash

curl -X POST \
  --url http://127.0.0.1:8083/connectors \
  --header 'content-type: application/json' \
  --data @sample-connector.json
