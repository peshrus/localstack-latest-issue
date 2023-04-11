#!/bin/bash
set -x
export DEFAULT_REGION=eu-central-1
awslocal s3 mb s3://test
awslocal s3api put-bucket-acl --bucket test --acl public-read &
awslocal sqs create-queue --queue-name test &
wait
set +x