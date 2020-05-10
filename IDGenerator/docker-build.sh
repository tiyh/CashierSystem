#!/bin/sh
#microk8s.docker build --build-arg local_prop_path=/tmp zk_address=zk-0.zk-hs.default.svc.cluster.local --build-arg zk_port=2181   --tag=cashier-istio-idgenerator:1 .
docker build --tag=cashier-istio-idgenerator:1 .