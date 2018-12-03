# sendgrid
An OMG service to access the Sendgrid email API

Usage
-----

```coffee
# Storyscript
sendgrid simple from: "sender@dummy.com" to: "recipient@dummy.com" subject: "Hello" content: "…"
sendgrid advanced
  personalizations: [
    to: [{email: "recipient1@dummy.com", name: "Mister Dummy"}],
    subject: "Dear Mr. Dummy",
    dynamic_template_data: {
      dynamic_parameter: "set with a value",
    }
  ],
  from: {email: "sender@dummy.com", name: "My sender name"}
  content: [{
    type: "text/plain",
    value: "Hello World!"
  }]
```
