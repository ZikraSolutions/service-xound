-- =============================================
-- XOUND - Schema de Base de Datos (PostgreSQL)
-- =============================================

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    status BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Tabla de canciones
CREATE TABLE IF NOT EXISTS songs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(200),
    tone VARCHAR(10),
    content TEXT,
    notes TEXT,
    user_id INT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_songs_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de eventos
CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    venue VARCHAR(200) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    share_code VARCHAR(20) UNIQUE,
    user_id INT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla pivote: setlist (relación evento <-> canción con orden)
CREATE TABLE IF NOT EXISTS setlist_songs (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    song_id INT NOT NULL,
    position INT NOT NULL,
    CONSTRAINT fk_setlist_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_setlist_song FOREIGN KEY (song_id) REFERENCES songs(id),
    CONSTRAINT uq_setlist_event_song UNIQUE (event_id, song_id)
);

-- Datos iniciales: roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('MUSICIAN'), ('GUEST')
ON CONFLICT (name) DO NOTHING;
