# _Sendgrid_ OMG Microservice

[![Open Microservice Guide](https://img.shields.io/badge/OMG%20Enabled-👍-green.svg?)](https://microservice.guide)

An OMG service to access the Sendgrid email API

## Direct usage in [Storyscript](https://storyscript.io/):

##### Send One
```coffee
sendgrid sendOne from: "sender@dummy.com" to: "recipient@dummy.com" subject: "Hello" content: "…"
```
##### Send Many
```coffee
sendgrid sendMany
  personalizations: [{
    "to": [{"email": "recipient1@dummy.com", "name": "Mister Dummy"}],
    "subject": "Dear Mr. Dummy",
    "dynamic_template_data": {
      "dynamic_parameter": "set with a value"
    }
  }]
  from: {"email": "sender@dummy.com", "name": "My sender name"}
  content: [{
    "type": "text/plain",
    "value": "Hello World!"
  }]
```

Curious to [learn more](https://docs.storyscript.io/)?

✨🍰✨

## Usage with [OMG CLI](https://www.npmjs.com/package/omg)

##### Send One
```shell
$ omg run sendOne -a from=<SENDER_EMAIL> -a to=<RECEIVER_EMAIL> -a subject=<EMAIL_SUBJECT> -a content=<CONTENT> -e SENDGRID_API_TOKEN=<SENDGRID_API_TOKEN>
```
##### Send Many
```shell
$ omg run sendMany -a personalizations=<PERSONALIZATION_OBJECT> -a from=<FROM_DETAILS_OBJECT> -a subject=<EMAIL_SUBJECT> -a content=<CONTENT_LIST> -e SENDGRID_API_TOKEN=<SENDGRID_API_TOKEN>
```

**Note**: The OMG CLI requires [Docker](https://docs.docker.com/install/) to be installed.

## License
[MIT License](https://github.com/omg-services/sendgrid/blob/master/LICENSE).
