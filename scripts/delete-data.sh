podman rm -f gymcrm-postgres
podman volume rm gymcrm_postgres_data
./run-postgres.sh