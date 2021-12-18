# GrandHotel

# Room

## Create

```shell
curl -i \
    -H "Accept: application/json" \
    -H "Content-Type: application/json"\
    -X POST -d "{\"number\":1,\"floor\":1,\"rooms\":1}" \
    http://localhost:8080/room
```

## Update

```shell
curl -i \
    -H "Accept: application/json" \
    -H "Content-Type: application/json"\
    -X PUT -d "{\"number\":1,\"floor\":2,\"rooms\":2}" \
    http://localhost:8080/room/1
```

## Get

```shell
curl http://localhost:8080/room/1
```

## Delete

```shell
curl -i \
    -H "Accept: application/json" \
    -H "Content-Type: application/json"\
    -X DELETE \
    http://localhost:8080/room/1
```

# Reservation

## Check availability

```shell
curl http://localhost:8080/reservation/availability?start=2021-12-01&end=2021-12-31
```

### Parameters

* `start` - stay from, date in format `YYYY-MM-DD`
* `end` - stay until, date in format `YYYY-MM-DD`

## Check reservations

```shell
curl http://localhost:8080/reservation?start=2021-12-01&end=2021-12-31
```

### Parameters

* `start` - stay from, date in format `YYYY-MM-DD`
* `end` - stay until, date in format `YYYY-MM-DD`
* `room` - room number

## Book vacant room

```shell
curl -i \
    -H "Accept: application/json" \
    -H "Content-Type: application/json"\
    -X POST -d '{"start":"2021-12-01","end":"2021-12-31","room":{"number":1,"rooms":1,"floor":1}}' \
    http://localhost:8080/reservation
```