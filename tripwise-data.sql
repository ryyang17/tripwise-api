--
-- PostgreSQL database dump
--

\restrict 7Rk1PdTxXhRtVAb0EZxHVDHe0rAl1Gg32lrqb9Aw67Ct4y5vziBsyWnuZDyYFIT

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.app_user (user_id, username, email, password, created_date, bio, location, favorite_destination, travel_style, notifications, currency) VALUES (1, 'string', 'string', 'string', '2025-10-05 13:23:57.246246', NULL, NULL, NULL, NULL, true, 'EUR');
INSERT INTO public.app_user (user_id, username, email, password, created_date, bio, location, favorite_destination, travel_style, notifications, currency) VALUES (2, 'test', 'test', 'string', '2025-10-20 00:40:05', NULL, NULL, NULL, NULL, true, 'USD');


--
-- Data for Name: trip; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.trip (trip_id, user_id, trip_name, start_date, end_date, destination, description, created_date, transportation) VALUES (5, 1, 'test 5', '2025-10-21', '2025-10-31', 'Germany', 'test 5 description', '2025-10-16 13:52:56.239', 'train');
INSERT INTO public.trip (trip_id, user_id, trip_name, start_date, end_date, destination, description, created_date, transportation) VALUES (4, 1, 'test xxx', '2025-10-07', '2025-10-15', 'The Netherlands', 'just a test at 10 okt', '2025-10-06 12:33:23.265', 'bus');
INSERT INTO public.trip (trip_id, user_id, trip_name, start_date, end_date, destination, description, created_date, transportation) VALUES (3, 1, 'Quick Weekend in Paris', '2025-10-12', '2025-10-14', 'Paris', 'djfsdlkvji', '2025-10-05 11:24:54.155', 'bus');
INSERT INTO public.trip (trip_id, user_id, trip_name, start_date, end_date, destination, description, created_date, transportation) VALUES (1, 2, 'test on userid 2', NULL, NULL, NULL, NULL, '2025-10-19 22:47:09.949198', NULL);


--
-- Data for Name: activity; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.activity (activity_id, trip_id, name, description, "time", location, estimated_cost, created_date) VALUES (2, 4, 'test', 'dsdv', '22:05', 'test22', 400.00, '2025-10-13 22:05:29.875801');


--
-- Data for Name: budget; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.budget (budget_id, trip_id, total_budget, currency, created_date) VALUES (33, 3, 400.00, 'USD', '2025-10-16 15:25:13.198');
INSERT INTO public.budget (budget_id, trip_id, total_budget, currency, created_date) VALUES (34, 5, 9000.00, 'EUR', '2025-10-16 22:24:24.553');


--
-- Data for Name: expense_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.expense_category (category_id, category_name, category_description, is_active, created_date) VALUES (1, 'Transportation', '', true, '2025-10-16 22:39:16.648115');
INSERT INTO public.expense_category (category_id, category_name, category_description, is_active, created_date) VALUES (2, 'Shopping', '', true, '2025-10-16 23:11:27.471337');


--
-- Data for Name: expense; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.expense (expense_id, trip_id, description, amount, expense_date, created_date, receipt_url, is_validated, category_id, original_amount, currency, exchange_rate, paid_by) VALUES (1, 5, 'fly test', 580.00, '2025-10-16', '2025-10-16 22:58:11.440674', '', false, 1, 580.00, 'EUR', 1.000000, NULL);
INSERT INTO public.expense (expense_id, trip_id, description, amount, expense_date, created_date, receipt_url, is_validated, category_id, original_amount, currency, exchange_rate, paid_by) VALUES (3, 5, 'ddd', 700.00, '2025-10-19', '2025-10-19 13:31:55.834777', '', false, 1, 499.98, 'EUR', 1.000000, NULL);
INSERT INTO public.expense (expense_id, trip_id, description, amount, expense_date, created_date, receipt_url, is_validated, category_id, original_amount, currency, exchange_rate, paid_by) VALUES (2, 5, 'citywalk', 600.00, '2025-10-16', '2025-10-16 23:11:55.724168', '', false, 2, 5000.00, 'CNY', 1.000000, NULL);
INSERT INTO public.expense (expense_id, trip_id, description, amount, expense_date, created_date, receipt_url, is_validated, category_id, original_amount, currency, exchange_rate, paid_by) VALUES (6, 5, 'test 3', 10.00, '2025-10-20', '2025-10-20 18:34:47.45489', NULL, false, 2, 10.00, 'EUR', 1.000000, 1);
INSERT INTO public.expense (expense_id, trip_id, description, amount, expense_date, created_date, receipt_url, is_validated, category_id, original_amount, currency, exchange_rate, paid_by) VALUES (5, 5, 'test 2 shared expense', 120.00, '2025-10-20', '2025-10-20 17:02:58.763227', NULL, false, 1, 120.00, 'EUR', 1.000000, 1);


--
-- Data for Name: expense_share; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.expense_share (id, expense_id, user_id, amount, is_settled) OVERRIDING SYSTEM VALUE VALUES (9, 6, 1, 4.0000, false);
INSERT INTO public.expense_share (id, expense_id, user_id, amount, is_settled) OVERRIDING SYSTEM VALUE VALUES (10, 6, 2, 6.0000, false);
INSERT INTO public.expense_share (id, expense_id, user_id, amount, is_settled) OVERRIDING SYSTEM VALUE VALUES (11, 5, 1, 75.0000, false);
INSERT INTO public.expense_share (id, expense_id, user_id, amount, is_settled) OVERRIDING SYSTEM VALUE VALUES (12, 5, 2, 45.0000, false);


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (1, '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'postgres', '2025-09-26 11:31:31.027073', 0, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (2, '2', 'add travel mode', 'SQL', 'V2__add_travel_mode.sql', -1745466015, 'postgres', '2025-10-01 15:46:27.295548', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (3, '3', 'create lowercase tables', 'SQL', 'V3__create_lowercase_tables.sql', -346722610, 'postgres', '2025-10-05 13:10:21.3147', 78, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (4, '4', 'add transportation to trip', 'SQL', 'V4__add_transportation_to_trip.sql', 512725712, 'postgres', '2025-10-06 13:14:51.496482', 12, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (5, '5', 'remove is active column', 'SQL', 'V5__remove_is_active_column.sql', -463092591, 'postgres', '2025-10-16 16:23:07.132547', 20, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (6, '6', 'add category foreign key to expense', 'SQL', 'V6__add_category_foreign_key_to_expense.sql', -691851300, 'postgres', '2025-10-16 22:57:55.537932', 33, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (7, '7', 'add profile fields to user', 'SQL', 'V7__add_profile_fields_to_user.sql', -1248203006, 'postgres', '2025-10-17 00:27:03.249833', 30, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (8, '8', 'add currency fields to expense', 'SQL', 'V8__add_currency_fields_to_expense.sql', 590778396, 'postgres', '2025-10-19 12:54:10.229663', 45, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (9, '9', 'update default currency to eur', 'SQL', 'V9__update_default_currency_to_eur.sql', 1845374462, 'postgres', '2025-10-19 13:12:25.970625', 11, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (10, '10', 'add participants and expense share', 'SQL', 'V10__add_participants_and_expense_share.sql', 755285362, 'postgres', '2025-10-19 23:04:27.905096', 43, true);


--
-- Data for Name: trip_participant; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.trip_participant (id, trip_id, user_id, role, joined_date) OVERRIDING SYSTEM VALUE VALUES (2, 5, 1, 'member', '2025-10-19 23:34:46.329234');
INSERT INTO public.trip_participant (id, trip_id, user_id, role, joined_date) OVERRIDING SYSTEM VALUE VALUES (7, 5, 2, NULL, '2025-10-20 15:41:32.945835');


--
-- Name: activity_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.activity_activity_id_seq', 2, true);


--
-- Name: app_user_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_user_user_id_seq', 1, false);


--
-- Name: budget_budget_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.budget_budget_id_seq', 34, true);


--
-- Name: expense_expense_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.expense_expense_id_seq', 6, true);


--
-- Name: expense_share_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.expense_share_id_seq', 12, true);


--
-- Name: expensecategory_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.expensecategory_category_id_seq', 2, true);


--
-- Name: trip_participant_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trip_participant_id_seq', 7, true);


--
-- Name: trip_trip_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trip_trip_id_seq', 5, true);


--
-- PostgreSQL database dump complete
--

\unrestrict 7Rk1PdTxXhRtVAb0EZxHVDHe0rAl1Gg32lrqb9Aw67Ct4y5vziBsyWnuZDyYFIT

