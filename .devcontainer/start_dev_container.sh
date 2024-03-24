docker build . -t wp-dev-container
docker run -it -v $(pwd)/../:/workspaces/Wild-Pets wp-dev-container /bin/bash
