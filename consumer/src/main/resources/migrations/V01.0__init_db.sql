CREATE SEQUENCE issues_id_seq;
CREATE TABLE m_issues(
  id INTEGER NOT NULL DEFAULT nextval('issues_id_seq'),
  issue_number INTEGER NOT NULL,
  repository_id INTEGER NOT NULL,
  repository_name VARCHAR(150) NOT NULL,
  issue_sender VARCHAR(100) NOT NULL,

  PRIMARY KEY(id)
);

CREATE UNIQUE INDEX issue_repository_unique ON m_issues(issue_number, repository_id);


CREATE SEQUENCE events_id_seq;
CREATE TABLE m_events(
id INTEGER NOT NULL DEFAULT nextval('events_id_seq'),
parent_id INTEGER NOT NULL,
parent_entity VARCHAR(50) NOT NULL,
payload TEXT NOT NULL,
created_at TIMESTAMP NOT NULL,

PRIMARY KEY(id),
FOREIGN KEY(parent_id) REFERENCES m_issues(id)
);
