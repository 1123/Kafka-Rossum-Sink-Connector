set -u
set -e

API_ENDPOINT=https://${COMPANY}.rossum.app/api/v1

echo API_ENDPOINT: $API_ENDPOINT


API_KEY=$(curl -s -H 'Content-Type: application/json' \
  -d '{"username": "'${ROSSUM_USER}'", "password": "'${PASSWORD}'"}' \
  $API_ENDPOINT/auth/login | jq -r '.key')

echo Retrieved an API KEY: $API_KEY

set -x
QUEUE_ID=$(curl -s -H 'Authorization: Bearer '${API_KEY} \
  $API_ENDPOINT'/queues?page_size=1' | jq -r '.results[0].id')

echo Retrieved a Queue ID: $QUEUE_ID

TASK_ID=$(curl -H 'Authorization: Bearer '${API_KEY} \
  -F content=@/tmp/foo.jpg "$API_ENDPOINT/uploads?queue=$QUEUE_ID" | jq -r .url)

set +x
