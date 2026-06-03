# 📋 Package Depth Validation

    
## 🎯 Overview
- Implement a validation system that determines whether a given folder in the Java package structure is allowed to have files imported/created, based on its depth relative to the `java/` root directory.
- 👷‍♂️ The class responsible for this is `PkgDepthValidator`
- **Location**: `app/src/main/java/mod/magd/pkgs/core/utils/PkgDepthValidator.java`
---

## 📁 Folder Structure & Depth Reference
Lets say we got 2 packages at the java folder `.sketchware/data/{projectId}/files/java`
   - `com.magd.app` (The main package)
   - `mod.raj.serv`
```
.sketchware/data/{projectId}/files/
    ┌───────────────┘
    │
    └── java/ →→→ [DEPTH 0] →→→ ❌ NO FILES ALLOWED
            │
            ├── com/ →→→ [DEPTH 1] →→→ ❌ NO FILES ALLOWED
            │       │
            │       └── magd/ →→→ [DEPTH 2] →→→ ✅ FILES ALLOWED (DEFAULT)
            │               │
            │               └── app/ →→→ [DEPTH 3+] →→→ ✅ FILES ALLOWED
            │                      │
            │                      ├── MainActivity.java
            │                      ├── Utils.java
            │                      └── Helper.java
            │
            │
            └── mod/ →→→ [DEPTH 1] →→→ ❌ NO FILES ALLOWED
                    │
                    └── raj/ →→→ [DEPTH 2] →→→ ✅ FILES ALLOWED
                            │
                            └── serv/ →→→ [DEPTH 3+] →→→ ✅ FILES ALLOWED
                                    │
                                    └── Service.java
```


### Depth Definition
- **DEPTH 0**: `java/` itself →→→ NO files allowed
- **DEPTH 1**: `java/com/` or `java/mod/` →→→ NO files allowed
- **DEPTH 2**: `java/com/magd/` or `java/mod/raj/` →→→ ✅ FILES ALLOWED (DEFAULT)
- **DEPTH 3+**: `java/com/magd/app/` or `java/mod/raj/serv` →→→ ✅ FILES ALLOWED

---
