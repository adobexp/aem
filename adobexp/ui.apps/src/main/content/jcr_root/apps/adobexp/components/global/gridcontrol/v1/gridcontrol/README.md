adobexp | Grid Control (v1)
====
Container component written in HTL.

## Features

* Configurable layout type.
* Configurable HTML ID attribute.
* Allowed components can be configured through policy configuration.

### Edit Dialog Properties
The following properties are written to JCR for this Container component and are expected to be available as `Resource` properties:

#### Container Properties
1. `./layoutColumns` - defines the layout type, with division of columns options in percentage like 50% by 50% etc.
2. `./id` - defines the component HTML ID attribute.

## BEM Description
```
BLOCK cmp-container
```

### Enabling Container Editing Functionality
The following property is required in the proxy component to enable full editing functionality for the Container:

1. `./cq:isContainer` - set to `{Boolean}true`, marks the Container as a container component

## Information
* **Vendor**: adobexp
* **Version**: v1
* **Compatibility**: AEM 6.5.11
* **Status**: production-ready
* **Author**: [Sharad Kumar](sharad@sharadtech.com)
