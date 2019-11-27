# _Sendgrid_ Open Microservice

> Access to the Sendgrid email API

[![Open Microservice Specification Version](https://img.shields.io/badge/Open%20Microservice-1.0-477bf3.svg)](https://openmicroservices.org)
[![Open Microservices Spectrum Chat](https://withspectrum.github.io/badge/badge.svg)](https://spectrum.chat/open-microservices)
[![Open Microservices Code of Conduct](https://img.shields.io/badge/Contributor%20Covenant-v1.4%20adopted-ff69b4.svg)](https://github.com/oms-services/.github/blob/master/CODE_OF_CONDUCT.md)
[![Open Microservices Commitzen](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://makeapullrequest.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

## Introduction

This project is an example implementation of the [Open Microservice Specification](https://openmicroservices.org), a standard
originally created at [Storyscript](https://storyscript.io) for building highly-portable "microservices" that expose the
events, actions, and APIs inside containerized software.

## Getting Started

The `oms` command-line interface allows you to interact with Open Microservices. If you're interested in creating an Open
Microservice the CLI also helps validate, test, and debug your `oms.yml` implementation!

See the [oms-cli](https://github.com/microservices/oms) project to learn more!

### Installation

```
npm install -g @microservices/oms
```

## Usage

### Open Microservices CLI Usage

Once you have the [oms-cli](https://github.com/microservices/oms) installed, you can run any of the following commands from
within this project's root directory:

#### Actions

##### sendOne

>

##### Action Arguments

| Argument Name      | Type     | Required | Default | Description                                                                                     |
| :----------------- | :------- | :------- | :------ | :---------------------------------------------------------------------------------------------- |
| from               | `string` | `true`   | None    | No description provided.                                                                        |
| to                 | `string` | `true`   | None    | No description provided.                                                                        |
| subject            | `string` | `true`   | None    | No description provided.                                                                        |
| content            | `string` | `true`   | None    | No description provided.                                                                        |
| content_type       | `string` | `true`   | None    | No description provided.                                                                        |
| SENDGRID_API_TOKEN | `string` | `true`   | None    | Create a Sendgrid account and register an API key at https://app.sendgrid.com/settings/api_keys |

```shell
oms run sendOne \
    -a from='*****' \
    -a to='*****' \
    -a subject='*****' \
    -a content='*****' \
    -a content_type='*****' \
    -e SENDGRID_API_TOKEN=$SENDGRID_API_TOKEN
```

##### sendMany

>

##### Action Arguments

| Argument Name      | Type     | Required | Default | Description                                                                                     |
| :----------------- | :------- | :------- | :------ | :---------------------------------------------------------------------------------------------- |
| personalizations   | `list`   | `true`   | None    | No description provided.                                                                        |
| from               | `object` | `true`   | None    | No description provided.                                                                        |
| subject            | `string` | `false`  | None    | No description provided.                                                                        |
| content            | `list`   | `true`   | None    | No description provided.                                                                        |
| reply_to           | `object` | `false`  | None    | No description provided.                                                                        |
| attachments        | `list`   | `false`  | None    | No description provided.                                                                        |
| template_id        | `string` | `false`  | None    | No description provided.                                                                        |
| sections           | `any`    | `false`  | None    | No description provided.                                                                        |
| headers            | `any`    | `false`  | None    | No description provided.                                                                        |
| categories         | `list`   | `false`  | None    | No description provided.                                                                        |
| custom_args        | `any`    | `false`  | None    | No description provided.                                                                        |
| send_at            | `int`    | `false`  | None    | No description provided.                                                                        |
| batch_id           | `string` | `false`  | None    | No description provided.                                                                        |
| asm                | `object` | `false`  | None    | No description provided.                                                                        |
| ip_pool_name       | `string` | `false`  | None    | No description provided.                                                                        |
| mail_settings      | `object` | `false`  | None    | No description provided.                                                                        |
| tracking_settings  | `any`    | `false`  | None    | No description provided.                                                                        |
| SENDGRID_API_TOKEN | `string` | `true`   | None    | Create a Sendgrid account and register an API key at https://app.sendgrid.com/settings/api_keys |

```shell
oms run sendMany \
    -a personalizations='*****' \
    -a from='*****' \
    -a subject='*****' \
    -a content='*****' \
    -a reply_to='*****' \
    -a attachments='*****' \
    -a template_id='*****' \
    -a sections='*****' \
    -a headers='*****' \
    -a categories='*****' \
    -a custom_args='*****' \
    -a send_at='*****' \
    -a batch_id='*****' \
    -a asm='*****' \
    -a ip_pool_name='*****' \
    -a mail_settings='*****' \
    -a tracking_settings='*****' \
    -e SENDGRID_API_TOKEN=$SENDGRID_API_TOKEN
```

## Contributing

All suggestions in how to improve the specification and this guide are very welcome. Feel free share your thoughts in the
Issue tracker, or even better, fork the repository to implement your own ideas and submit a pull request.

[![Edit sendgrid on CodeSandbox](https://codesandbox.io/static/img/play-codesandbox.svg)](https://codesandbox.io/s/github/oms-services/sendgrid)

This project is guided by [Contributor Covenant](https://github.com/oms-services/.github/blob/master/CODE_OF_CONDUCT.md).
Please read out full [Contribution Guidelines](https://github.com/oms-services/.github/blob/master/CONTRIBUTING.md).

## Additional Resources

- [Install the CLI](https://github.com/microservices/oms) - The OMS CLI helps developers create, test, validate, and build
  microservices.
- [Example OMS Services](https://github.com/oms-services) - Examples of OMS-compliant services written in a variety of
  languages.
- [Example Language Implementations](https://github.com/microservices) - Find tooling & language implementations in Node,
  Python, Scala, Java, Clojure.
- [Storyscript Hub](https://hub.storyscript.io) - A public registry of OMS services.
- [Community Chat](https://spectrum.chat/open-microservices) - Have ideas? Questions? Join us on Spectrum.
