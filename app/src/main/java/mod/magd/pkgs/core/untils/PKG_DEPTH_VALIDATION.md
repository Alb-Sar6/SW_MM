# 📋 Folder Depth Validation & File (Creation & Import) Restrictions - Complete Plan
    - 👷‍♂️ The class responsible for this is `PkgDepthValidator`
    - **Location**: `app/src/main/java/mod/magd/pkgs/core/utils/PkgDepthValidator.java`
## 🎯 Overview
Implement a validation system that determines whether a given folder in the Java package structure is allowed to have files imported/created, based on its depth relative to the `java/` root directory.

---

## 📁 Folder Structure & Depth Reference

.sketchware/data/{projectId}/files/
    └── java/ [DEPTH 0] ❌ NO FILES ALLOWED
            ├── com/ [DEPTH 1] ❌ NO FILES ALLOWED
            │       └── magd/ [DEPTH 2] ✅ FILES ALLOWED (DEFAULT)
            │               └── zzz/ [DEPTH 3+] ✅ FILES ALLOWED
            │                      ├── MainActivity.java
            │                      ├── Utils.java
            │                      └── Helper.java
            └── mod/ [DEPTH 1] ❌ NO FILES ALLOWED
                    └── mmm/ [DEPTH 2] ✅ FILES ALLOWED
                            └── hk/ [DEPTH 3+] ✅ FILES ALLOWED
                                    └── Service.java



### Depth Definition
- **DEPTH 0**: `java/` itself → NO files
- **DEPTH 1**: `java/com/` or `java/mod/` → NO files  
- **DEPTH 2**: `java/com/magd/` or `java/mod/mmm/` → ✅ FILES ALLOWED (DEFAULT)
- **DEPTH 3+**: `java/com/magd/zzz/` → ✅ FILES ALLOWED

---
