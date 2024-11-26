#!/bin/bash
set -e

# Wait for MongoDB to start
sleep 10

mongo <<EOF
use admin
db.createUser({
    user: "mongoadmin",
    pwd: "mongoadmin123",
    roles: [
        { role: "root", db: "admin" }
    ]
})

use testdb
db.createUser({
    user: "testuser",
    pwd: "testpassword",
    roles: [
        { role: "readWrite", db: "testdb" }
    ]
})
EOF