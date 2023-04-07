db.createUser(
    {
        user: "user",
        pwd: "password",
        roles: [
            {
                role: "readWrite",
                db: "rxdb"
            }
        ]
    }
);

db.createCollection('users');
db.createCollection('goods');
