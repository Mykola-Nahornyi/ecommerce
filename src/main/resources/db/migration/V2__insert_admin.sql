INSERT INTO users (email, password, first_name, last_name, roles)
VALUES (
  'admin@example.com',
  '$2a$10$nzk.lp5DX/8XgvpbFMSpmO3XAWfq.PN1dQzAZMTrDwn7JM6T4.VP.',
  'Admin',
  'Admin',
  'ROLE_ADMIN'
)
ON CONFLICT (email) DO NOTHING;
