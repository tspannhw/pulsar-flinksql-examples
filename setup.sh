docker exec -it pulsar bin/pulsar-admin topics create persistent://public/default/events

docker exec -it pulsar bin/pulsar-admin topics list public/default

docker exec -it pulsar bin/pulsar-admin topics set-retention -s -1 -t -1 persistent://public/default/events
docker exec -it pulsar bin/pulsar-admin topics get-retention persistent://public/default/events