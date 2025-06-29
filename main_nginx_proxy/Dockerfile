FROM nginx:latest

COPY main_nginx_proxy.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
