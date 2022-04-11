# json-schema-validator
Simple json-schema-validator

### Example:
```java
JSONValidator validator = new JSONValidator()
        .schema(<path_to_schema>)
        .json(<path_to_json_that_will_be_validated_against_the_schema>);

if (!validator.isValid()) {
    validator.getErrors().forEach(System.out::println);
}
```

### You can specify schema and json either by their absolute paths or by providing InputStreams of them.
### You can specify json-schema draft version by passing it to the overloaded ctor.

### Example:
```java
JSONValidator validator = new JSONValidator(SpecVersion.VersionFlag.V6)
        .schema(getClass().getResourceAsStream("resources/schema.json"))
        .json(getClass().getResourceAsStream("resources/data.json"));

if (!validator.isValid()) {
    validator.getErrors().forEach(System.out::println);
}
```

### Output:
```java
$.contact.name: is missing but it is required
$.message: may only be 10 characters long
$.contact.phone: must be in E.164 format starting with US prefix (+1)
```

### Sample Schema:
```javascript
{
    "$schema": "http://json-schema.org/draft-07/schema",
    "type": "object",
    "properties": {
        "country": {
            "enum": ["US", "CA"]
        },
        "contact": {
            "type": "object",
            "properties": {
                "name": {
                    "type": "string"
                },
                "phone": {
                    "type": "string"
                }
            },
            "required": ["name", "phone"]
        },
        "message": {
            "type": "string",
            "minLength": 0,
            "maxLength": 10
        }
    },
    "required": ["country", "contact", "message"],
    "allOf": [
        {
            "if": {
                "properties": {
                    "country": {
                        "const": "US"
                    }
                },
                "required": [ "country" ]
            },
            "then": {
                "properties": {
                    "contact": {
                        "properties": {
                            "phone": {
                                "type": "string",
                                "pattern": "^\\+1[0-9]{3,13}$"
                            }
                        },
                        "message": {
                            "pattern": "$.contact.phone: must be in E.164 format starting with US prefix (+1)"
                        }
                    }
                }
            }
        },
        {
            "if": {
                "properties": {
                    "country": {
                        "const": "CA"
                    }
                },
                "required": ["country"]
            },
            "then": {
                "properties": {
                    "contact": {
                        "properties": {
                            "phone": {
                                "type": "string",
                                "pattern": "^\\+44[0-9]{3,13}$"
                            },
                            "message": {
                                "pattern": "$.contact.phone: must be in E.164 format starting with UK prefix (+44)"
                            }
                        }
                    }
                }
            }
        }
    ]
}
```

### Sample JSON:
```javascript
{
  "message": "Hello world!",
  "country": "US",
  "contact": {
    "phone": "+44123456789"
  }
}
```
