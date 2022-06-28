
FROM debian:11
WORKDIR /app
EXPOSE 9222
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -q -y \
chromium

ENV LC_ALL ru_RU.UTF-8
ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU.UTF-8

CMD [ "chromium", "--headless", "--remote-debugging-port=9222", "--no-sandbox", "https://luwrain.org" ]
