ALTER TABLE m_events ADD COLUMN event_action varchar(25) NOT NULL DEFAULT 'opened';
ALTER TABLE m_events ADD COLUMN  sender varchar(100) NOT NULL;

CREATE UNIQUE INDEX unique_opened_issue_event ON m_events(parent_entity, parent_id) WHERE event_action = 'opened';

ALTER TABLE m_events DROP CONSTRAINT m_events_parent_id_fkey,
ADD CONSTRAINT m_events_parent_id_fkey FOREIGN KEY(parent_id) REFERENCES m_issues(id) ON DELETE CASCADE;