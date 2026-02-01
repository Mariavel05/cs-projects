# Cyber Crime Investigation

This project implements a cyber crime investigation system that tracks and analyzes hacker activity using core data structures. The system efficiently manages a large set of hacker records and their associated incidents.

## Overview
The database is implemented as a dynamically resizing hash table with separate chaining (an array of linked lists). Each hacker may be associated with multiple incidents, and the system supports efficient insertion, searching, deletion, and analysis of records as data grows.

## Key Concepts
- Hash tables with separate chaining
- Dynamic resizing and rehashing
- Linked list insertion and deletion
- Priority queue (max-heap) for ranking hackers
- Handling duplicates and edge cases

## Functionality
- Insert hackers and incidents while preventing duplicate entries
- Search for hackers by name
- Remove hackers using linked-list deletion
- Merge duplicate hacker identities based on incident counts
- Retrieve the top N most wanted hackers using a priority queue
- Query hackers by incident location

## My Contributions
I implemented all core logic in `CyberCrimeInvestigation.java`, including collision handling, resizing and rehashing, deletion, merging hacker identities, and analytical queries. This project emphasized correctness, efficiency, and careful reasoning about edge cases and data structure invariants.

