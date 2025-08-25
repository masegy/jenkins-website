FROM ubi9/nginx-118
LABEL origin="default"
COPY dist/ .
ADD nginx.conf "${NGINX_CONF_PATH}"

CMD ["nginx", "-g", "daemon off;"]