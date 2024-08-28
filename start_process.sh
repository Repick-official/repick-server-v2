#!/bin/bash

ECR_REPOSITORY="654654448479.dkr.ecr.ap-northeast-2.amazonaws.com/repick-repo"
ECR_REPOSITORY="repick-repo"
ECR_DOCKER_TAG="latest"

aws ecr get-login-password --region ap-northeast-2 \
  | docker login --username AWS --password-stdin ${ECR_REPOSITORY};

export IMAGE=${ECR_REPOSITORY};
export TAG=${ECR_DOCKER_TAG};
sudo docker pull ${{ env.ECR_REGISTRY }}/${{ env.ECR_REPOSITORY }}
sudo docker run -d --name dc -p 8080:8080 ${{ env.ECR_REGISTRY }}/${{ env.ECR_REPOSITORY }}
sudo docker image prune -f
