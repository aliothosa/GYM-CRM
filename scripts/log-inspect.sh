#!/bin/bash

SERVICES=$(docker compose ps --services)

for SERVICE in $SERVICES; do
    clear

    echo "======================================"
    echo " Logs for service: $SERVICE"
    echo "======================================"
    echo

    docker compose logs --tail=30 "$SERVICE"

    echo
    read -r -p "Press ENTER to continue..."
done

clear