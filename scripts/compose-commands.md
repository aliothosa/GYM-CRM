
## From the repository root:

` podman compose up --build -d`
## Stop containers while keeping volumes/data:

`podman compose stop`

## Start stopped containers again:

`podman compose start`

## Stop and remove containers/networks (keeps named volumes unless -v is added):

`podman compose down`

## To rebuild only after source changes:

`podman compose build`
`podman compose up -d`
