
set -euo pipefail

CONNECT_URL="${CONNECT_URL:-http://localhost:8083}"

for file in "$(dirname "$0")"/*-connector.json; do
  echo "Registering $(basename "$file") ..."
  curl -s -X POST \
    -H "Content-Type: application/json" \
    -d "@$file" \
    "$CONNECT_URL/connectors" \
    -w "\nHTTP %{http_code}\n"
done

echo "Mevcut connectorlar:"
curl -s "$CONNECT_URL/connectors" && echo
