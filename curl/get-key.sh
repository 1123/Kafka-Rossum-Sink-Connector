curl -s -H 'Content-Type: application/json' \
  -d '{"username": "'${ROSSUM_USER}'", "password": "'${PASSWORD}'"}' \
  'https://'${COMPANY}'.rossum.app/api/v1/auth/login' | jq -r '.key'

