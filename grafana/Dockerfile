FROM grafana/grafana:7.0.3

WORKDIR /

ADD ./provisioning /etc/grafana/provisioning
ADD ./grafana.ini /etc/grafana
ADD ./dashboards /var/lib/grafana/dashboards
