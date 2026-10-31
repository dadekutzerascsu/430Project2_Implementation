# Project 2 Implementation Checklist

**Implementation and submission due: October 31, 2026**

This checklist is based on the [Stage 1 assignment](./Project2%20Implementation%20Stage%201%20Fall%2026%20(1).docx) and [Project 2 Class Diagrams](./Project%202%20Class%20Diagrams.docx). Checkmarks reflect only what is verifiable in this repository; D2L and Zoom submissions cannot be verified here.

## Already present in this repository

- [x] Project 1 warehouse/backend code is present under `src/`.
- [x] The Project 2 class-diagram document is present in `Project2requirements/`.
- [ ] Confirm the class designs were submitted to D2L by October 25; this cannot be verified from the repository.

## Implementation

- [x] Implement the text-only, matrix-based FSM interface, following the Library FSM pattern.
- [x] Centralize the state-transition matrix and transition handling in `Context`.
- [x] Implement the four diagram states: `OpeningState`, `ClientMenuState`, `ClerkMenuState`, and `ManagerMenuState`.
- [x] Keep the change to the user interface and reuse the Project 1 backend.
- [x] In `OpeningState`, offer client, clerk, and manager entry; validate the client ID for client entry.
- [x] In `ClientMenuState`, show client details, products, and transactions; add/display wishlist entries; place orders; and log out.
- [x] In `ClerkMenuState`, add clients; show products and clients; show outstanding balances; record payments; become a client; and log out.
- [x] In `ManagerMenuState`, add products; display waitlists; receive shipments; become a clerk; and log out.
- [x] Store active-client and prior-role information in `Context` so logout returns to the correct state.

## Compile and demonstrate the required workflow

- [x] Put all application source files in the repository's `WarehouseFSM/` folder.
- [x] Compile the FSM and run the assignment workflow through the menus (`FsmWorkflowTest`).
- [x] As clerk, create clients C1–C5 and display them with balances.
- [x] As manager, create P1–P5 with quantities 10, 20, 30, 40, 50 and unit prices $1, $2, $3, $4, $5; display them as clerk.
- [x] As C1, add 5 each of P1, P3, P5; display wishlist; return to clerk.
- [x] As C2, add 7 each of P1, P2, P4; display wishlist; return to clerk.
- [x] Return to manager and login; as C3, add 6 each of P1, P2, P5; display wishlist; log out.
- [x] As C2, order everything available in the wishlist.
- [x] As clerk, display clients; become C3 and order everything available in the wishlist.
- [x] Return to clerk; display all clients and those with outstanding balances.
- [x] Verify transitions, logout paths, order totals, stock, and waitlist results through the menu test.

## Video and D2L submission

- [ ] Publish/push the `WarehouseFSM/` folder to the group's GitHub repository.
- [ ] Record a Zoom meeting using a MinnState Zoom account, with screen sharing and audio; record to the cloud.
- [ ] During the recording, open the GitHub repository, compile and run the program, and demonstrate every step in the workflow above.
- [ ] Speak clearly and ensure all group members participate fully.
- [ ] End the recording, meeting, and Zoom session; retrieve the public video link when it is available.
- [ ] Confirm class designs were submitted to D2L by October 25.
- [ ] Submit one completion report per group to D2L by October 31, describing work sharing and including GitHub and video links.
