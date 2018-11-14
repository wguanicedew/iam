FROM centos:7

COPY EGI-trustanchors.repo igi-test-ca.repo /etc/yum.repos.d/
COPY ./update-trust-anchors.sh /

RUN yum -y install epel-release && yum -y update
RUN yum -y install rsync igi-test-ca ca-policy-egi-core fetch-crl && fetch-crl || true
RUN chmod +x /update-trust-anchors.sh 

RUN FORCE_TRUST_ANCHORS_UPDATE=1 /update-trust-anchors.sh

ENTRYPOINT ["/update-trust-anchors.sh"]
