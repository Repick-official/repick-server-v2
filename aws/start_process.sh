#!/bin/bash

set -e

ECR_REGISTRY=$(aws ssm get-parameter --name "ECR_REGISTRY" --with-decryption --region ap-northeast-2 --query "Parameter.Value" --output text)
ECR_DOCKER_TAG=latest

echo $ECR_REGISTRY
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REGISTRY

sudo docker pull $ECR_REGISTRY:$ECR_DOCKER_TAG

sudo docker run -d --name dc -p 8080:8080 $ECR_REGISTRY:$ECR_DOCKER_TAG

sudo docker image prune -f