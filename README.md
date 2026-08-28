# HES Hunt

![GitHub License](https://img.shields.io/github/license/MeecoLabs/HEShunt-android?style=for-the-badge)
![Homepage](https://img.shields.io/badge/homepage-blue?style=for-the-badge&link=https%3A%2F%2Fapps.meecolabs.eu%2Fhes-hunt)
![F-Droid Version](https://img.shields.io/f-droid/v/eu.meecolabs.heshunt?baseUrl=https%3A%2F%2Fapps.meecolabs.eu%2Frepo&style=for-the-badge)
![GitHub Release Date](https://img.shields.io/github/release-date/MeecoLabs/HEShunt-android?style=for-the-badge)

A simple Android app to track Historic Environment Scotland’s History Hunt collectible cards.

## Features

- List of cards sorted by status: Collected, Available, Missed, Future.
- Map view of Historic Scotland properties.
  - Filter map to show only properties where missing cards can be found.
- Direct links to property websites for opening times.

## Data Sources

### History Hunt Cards

The latest up-to-date information about the history hunt can be found on the [HES website](https://www.historicenvironment.scot/visit/membership-and-passes/membership/the-history-hunt/). This does not include the historic information though, so some manual research had to be done.

- **2024 Collection**: Initial set of 7 main cards + 3 rare cards.
  - First mentioned in [Summer 2024 Members Magazine](https://issuu.com/thinkpublishing/docs/hes_summer_2024) without a concrete list of cards or availability.
  - Data compiled from [archived website](https://web.archive.org/web/20240619210946/https://members.historic-scotland.gov.uk/history-hunt) and sponsored blog [Monkey and Mouse](https://monkeyandmouse.co.uk/historic-scotland-collectible-cards-the-history-hunt/).
- **2025 Additions**: Same 7 main cards + 4 rare cards.
  - First mentioned in [Summer 2025 Members Magazine](https://issuu.com/historic-scotland/docs/historic_scotland_summer_2025) without a concrete list of cards or availability.
  - Data compiled from [archived website](https://web.archive.org/web/20250422122311/https://members.historic-scotland.gov.uk/history-hunt), [Halloween announcement](https://web.archive.org/web/20260216025421/https://www.historicenvironment.scot/about-us/news/halloween-descends-on-historic-sites/), [Mythical Mystery Hunt](https://web.archive.org/web/20251101141255/https://www.historicenvironment.scot/visit-a-place/whats-on/event/?eventId=6453bd95-11fc-4168-929d-b340009976c4).
- **2026 Additions**: Same 7 main cards + 6 rare cards.
  - First mentioned in [Spring 2026 Members Magazine](https://issuu.com/historic-scotland/docs/historic_scotland_spring_2026).
  - Data compiled from [archived website](https://web.archive.org/web/20260622113418/https://www.historicenvironment.scot/visit/membership-and-passes/membership/the-history-hunt/) and private enquiry with HES.

### HES Properties (Sites)

- **Endpoint**: `https://inspire.hes.scot/arcgis/rest/services/HES/Properties_in_care_points/MapServer/0/query?where=1%3D1&outFields=*&f=geojson`
- **Mapping**: Sites are matched using the stable `PIC_ID` from this endpoint. This ensures that even if property names change or vary slightly between sources, the link between cards and locations remains reliable.

## Tech Stack

Built with modern Android technologies including Kotlin, Jetpack Compose, Koin, and Room.

### Handcrafted with AI Assistance

While the core logic, UX/UI and architecture are "handcrafted", I leveraged agentic AI coding assistants to streamline development. This was particularly useful for automating mundane, repetitive tasks that traditional IDE features do not yet support (boilerplate generation, complex cross-file refactoring).

For more details on the technical setup, see [AGENT.md](./AGENT.md).

## Releases 

Hopefully coming soon.

Look out for the homepage and a custom F-Droid repository to appear.

## Disclaimer

This application is not affiliated with, maintained, authorized, endorsed or sponsored by Historic Environment Scotland or any of its affiliates. This is an unofficial implementation.
