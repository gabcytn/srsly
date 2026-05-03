CREATE TABLE users(
	id UUID PRIMARY KEY NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
	password VARCHAR,
	email_verified_at TIMESTAMP(6) WITHOUT TIME ZONE,
	is_subscribed_to_mail_reminders BOOLEAN,
	created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE problems(
	id BIGINT PRIMARY KEY NOT NULL,
	frontend_id INTEGER UNIQUE NOT NULL,
	title VARCHAR(255) NOT NULL,
	question TEXT NOT NULL,
	difficulty VARCHAR(15) CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')) NOT NULL,
	url VARCHAR(255) NOT NULL
);

CREATE INDEX idx_problems_difficulty ON problems(difficulty); 
CREATE INDEX idx_problems_title ON problems(title);

CREATE TABLE solved_problems(
    id BIGINT PRIMARY KEY NOT NULL,
	problem_id BIGINT NOT NULL REFERENCES problems(id),
	user_id UUID NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_solved_problems_problem_id ON solved_problems(problem_id);
CREATE INDEX idx_solved_problems_user_id ON solved_problems(user_id);

CREATE TABLE review_problems(
    id BIGINT PRIMARY KEY NOT NULL,
	solved_problem_id BIGINT REFERENCES solved_problems(id) NOT NULL,
	status VARCHAR(255) CHECK (status IN ('NEW', 'LEARNING', 'REVIEWING', 'MASTERED')) NOT NULL,
	ease_factor DOUBLE PRECISION NOT NULL,
	repetitions INTEGER NOT NULL,
	interval INTEGER NOT NULL,
	last_attempt_at DATE NOT NULL,
	next_attempt_at DATE NOT NULL,
	created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_review_problems_next_attempt_at ON review_problems(next_attempt_at);
CREATE INDEX idx_review_problems_solved_problem ON review_problems(solved_problem_id);

CREATE TABLE solutions(
    id BIGINT PRIMARY KEY NOT NULL,
	solved_problem_id BIGINT REFERENCES solved_problems(id) NOT NULL,
	title VARCHAR(255) NOT NULL,
	code TEXT NOT NULL,
	ai_critique JSONB,
	note TEXT,
	created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_solutions_solved_problem_id ON solutions(solved_problem_id);

CREATE TABLE review_attempts(
    id BIGINT PRIMARY KEY NOT NULL,
	solved_problem_id BIGINT REFERENCES solved_problems(id) NOT NULL,
	ease_factor DOUBLE PRECISION NOT NULL,
	grade INTEGER,
	attempted_at DATE NOT NULL,
	created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_review_attempts_solved_problem_id ON review_attempts(solved_problem_id);

CREATE TABLE tags(
    id BIGINT PRIMARY KEY NOT NULL,
	name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE problem_tags(
    problem_id BIGINT REFERENCES problems(id) NOT NULL,
	tag_id BIGINT REFERENCES tags(id) NOT NULL,
	PRIMARY KEY (problem_id, tag_id)
);
