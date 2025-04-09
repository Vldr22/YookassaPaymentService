CREATE SEQUENCE IF NOT EXISTS public.app_settings_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.card_tokens_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.employees_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.registration_codes_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.sms_codes_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.subscription_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.app_settings_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS public.app_settings (
                                                   id integer NOT NULL DEFAULT nextval('public.app_settings_seq'),
                                                   created_at timestamp(6) without time zone NOT NULL,
                                                   updated_at timestamp(6) without time zone NOT NULL,
                                                   module character varying(255) NOT NULL,
                                                   setting character varying(255) NOT NULL,
                                                   value character varying(255) NOT NULL
);

ALTER TABLE public.app_settings OWNER TO postgres;

ALTER TABLE public.app_settings
    ADD CONSTRAINT app_settings_module_key UNIQUE (module),
    ADD CONSTRAINT app_settings_pkey PRIMARY KEY (id),
    ADD CONSTRAINT app_settings_setting_key UNIQUE (setting);

CREATE TABLE IF NOT EXISTS public.card_tokens (
                                                  id bigint NOT NULL DEFAULT nextval('public.card_tokens_seq'),
                                                  client_id uuid NOT NULL,
                                                  token text NOT NULL
);

ALTER TABLE public.card_tokens OWNER TO postgres;

ALTER TABLE public.card_tokens
    ADD CONSTRAINT card_tokens_client_id_key UNIQUE (client_id),
    ADD CONSTRAINT card_tokens_pkey PRIMARY KEY (id),
    ADD CONSTRAINT card_tokens_token_key UNIQUE (token);

CREATE TABLE IF NOT EXISTS public.clients (
                                              blocked boolean,
                                              registration_date timestamp(6) without time zone NOT NULL,
                                              id uuid NOT NULL,
                                              midname character varying(50),
                                              name character varying(50) NOT NULL,
                                              surname character varying(50) NOT NULL,
                                              phone text NOT NULL,
                                              role character varying(255) NOT NULL,
                                              CONSTRAINT clients_role_check CHECK (((role)::text = ANY ((ARRAY['ROLE_EMPLOYEE'::character varying, 'ROLE_CLIENT'::character varying, 'ROLE_ADMIN'::character varying])::text[])))
);

ALTER TABLE public.clients OWNER TO postgres;

ALTER TABLE public.clients
    ADD CONSTRAINT clients_phone_key UNIQUE (phone),
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS public.employees (
                                                created_at timestamp(6) without time zone NOT NULL,
                                                id bigint NOT NULL DEFAULT nextval('public.employees_seq'),
                                                updated_at timestamp(6) without time zone NOT NULL,
                                                email character varying(50) NOT NULL,
                                                midname character varying(50),
                                                name character varying(50) NOT NULL,
                                                surname character varying(50) NOT NULL,
                                                password character varying(255) NOT NULL,
                                                role character varying(255) NOT NULL,
                                                CONSTRAINT employees_role_check CHECK (((role)::text = ANY ((ARRAY['ROLE_EMPLOYEE'::character varying, 'ROLE_CLIENT'::character varying, 'ROLE_ADMIN'::character varying])::text[])))
);

ALTER TABLE public.employees OWNER TO postgres;

ALTER TABLE public.employees
    ADD CONSTRAINT employees_email_key UNIQUE (email),
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS public.registration_codes (
                                                         used boolean NOT NULL,
                                                         expires_at timestamp(6) without time zone NOT NULL,
                                                         id bigint NOT NULL DEFAULT nextval('public.registration_codes_seq'),
                                                         code character varying(50) NOT NULL,
                                                         email character varying(50) NOT NULL,
                                                         role character varying(255) NOT NULL,
                                                         CONSTRAINT registration_codes_role_check CHECK (((role)::text = ANY ((ARRAY['ROLE_EMPLOYEE'::character varying, 'ROLE_CLIENT'::character varying, 'ROLE_ADMIN'::character varying])::text[])))
);

ALTER TABLE public.registration_codes OWNER TO postgres;

ALTER TABLE public.registration_codes
    ADD CONSTRAINT registration_codes_code_key UNIQUE (code),
    ADD CONSTRAINT registration_codes_email_key UNIQUE (email),
    ADD CONSTRAINT registration_codes_pkey PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS public.sms_codes (
                                                code character varying(4) NOT NULL,
                                                create_date timestamp(6) without time zone NOT NULL,
                                                expire_time timestamp(6) without time zone,
                                                id bigint NOT NULL DEFAULT nextval('public.sms_codes_seq'),
                                                update_date timestamp(6) without time zone,
                                                phone character varying(255) NOT NULL,
                                                status character varying(255) NOT NULL,
                                                CONSTRAINT sms_codes_status_check CHECK (((status)::text = ANY ((ARRAY['CREATED'::character varying, 'SEND'::character varying, 'VERIFIED'::character varying, 'EXPIRED'::character varying])::text[])))
);

ALTER TABLE public.sms_codes OWNER TO postgres;

ALTER TABLE public.sms_codes
    ADD CONSTRAINT sms_codes_pkey PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS public.subscription (
                                                   active boolean NOT NULL,
                                                   end_time date NOT NULL,
                                                   start_time date NOT NULL,
                                                   id bigint NOT NULL DEFAULT nextval('public.subscription_id_seq'),
                                                   client_id uuid NOT NULL,
                                                   subscription_type character varying(255) NOT NULL,
                                                   CONSTRAINT subscription_subscription_type_check CHECK (((subscription_type)::text = 'DEFAULT'::text))
);

ALTER TABLE public.subscription OWNER TO postgres;

ALTER TABLE public.subscription
    ADD CONSTRAINT subscription_client_id_key UNIQUE (client_id),
    ADD CONSTRAINT subscription_pkey PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS public.transactions (
                                                   amount bigint NOT NULL,
                                                   card_token_id bigint,
                                                   create_date timestamp(6) without time zone NOT NULL,
                                                   fee bigint NOT NULL,
                                                   fee_percent bigint NOT NULL,
                                                   update_date timestamp(6) without time zone,
                                                   client_id uuid NOT NULL,
                                                   id uuid NOT NULL,
                                                   currency character varying(255) NOT NULL,
                                                   description character varying(255) NOT NULL,
                                                   status text NOT NULL,
                                                   CONSTRAINT transactions_currency_check CHECK (((currency)::text = 'RUB'::text)),
                                                   CONSTRAINT transactions_status_check CHECK ((status = ANY (ARRAY['NEW'::text, 'IN_PROGRESS'::text, 'DONE'::text, 'CANCELED'::text, 'REFUNDED'::text])))
);

ALTER TABLE public.transactions OWNER TO postgres;

ALTER TABLE public.transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (id);

ALTER TABLE public.transactions
    ADD CONSTRAINT fk1cmqgln21d49plwgufjvl5ios FOREIGN KEY (card_token_id) REFERENCES public.card_tokens(id);

ALTER TABLE public.card_tokens
    ADD CONSTRAINT fkbvhrjbsot8nwyx0bx41mssdwx FOREIGN KEY (client_id) REFERENCES public.clients(id);

ALTER TABLE public.transactions
    ADD CONSTRAINT fkjp6w7dmqrj0h9vykk2pbtik2 FOREIGN KEY (client_id) REFERENCES public.clients(id);

ALTER TABLE public.subscription
    ADD CONSTRAINT fkmgdo6ymscw0s5677kyvnwf34w FOREIGN KEY (client_id) REFERENCES public.clients(id);

ALTER SEQUENCE public.app_settings_seq OWNER TO postgres;
ALTER SEQUENCE public.card_tokens_seq OWNER TO postgres;
ALTER SEQUENCE public.employees_seq OWNER TO postgres;
ALTER SEQUENCE public.registration_codes_seq OWNER TO postgres;
ALTER SEQUENCE public.sms_codes_seq OWNER TO postgres;
ALTER SEQUENCE public.subscription_id_seq OWNER TO postgres;
ALTER SEQUENCE public.app_settings_id_seq OWNER TO postgres;

ALTER SEQUENCE public.app_settings_id_seq OWNED BY public.app_settings.id;
ALTER SEQUENCE public.subscription_id_seq OWNED BY public.subscription.id;
