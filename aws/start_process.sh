#!/bin/bash

set -e

ECR_REGISTRY=654654448479.dkr.ecr.ap-northeast-2.amazonaws.com/repick-repo
ECR_DOCKER_TAG=latest

echo $ECR_REGISTRY
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REGISTRY

sudo docker pull $ECR_REGISTRY:$ECR_DOCKER_TAG

sudo docker run -d --name dc -p 8080:8080 $ECR_REGISTRY:$ECR_DOCKER_TAG

sudo docker image prune -f