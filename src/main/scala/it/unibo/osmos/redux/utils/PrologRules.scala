package it.unibo.osmos.redux.utils

object PrologRules {

  val rules =
    """
               pow(X, 0, 1):-!.
               pow(X, 1, X):-!.
               pow(X, N, R) :-
                    N1 is N-1,
                    pow(X, N1, RT), R is X*RT.

               getLength([X,Y], L):-
                   pow(X,2,PX),
                   pow(Y,2,PY),
                   L is sqrt(PX+PY).

               add([X1,Y1], [X2,Y2], [RX,RY]) :-
                    RX is X1+X2,
                    RY is Y1+Y2.

               subtract([X1,Y1], [X2, Y2], [RX, RY]):-
                   RX is X1-X2,
                   RY is Y1-Y2,!.

               multiply([X,Y], V, [VX,VY]):-
                   VX is X*V,
                   VY is Y*V,!.

               divide([X,Y],V,[VX,VY]):- divide(X,V,VX),divide(Y,V,VY),!.

               divide(X,Y,R) :- ( Y \= 0 -> R is X / Y ; R is X ).

               unitVector([PX1,PY1],[PX2,PY2],[VX,VY]) :-
                   subtract([PX1,PY1], [PX2,PY2], [UVX,UVY]),
                   pow(UVX,2,PUVX),
                   pow(UVY,2,PUVY),
                   MOD is sqrt(PUVX+PUVY),
                   divide([UVX,UVY],MOD, [VX,VY]).

               euclideanDistance([PX1,PY1],[PX2,PY2],D) :-
                   pow(PX1 - PX2, 2, PX),
                   pow(PY1 - PY2, 2, PY),
                   D is sqrt(PX + PY).

               getNewLength([X,Y], NewLength, [RX, RY]) :-
                   getLength([X,Y],Length),
                   divide(NewLength, Length, TEMP),
                   multiply([X,Y],TEMP,[RX,RY]).

               limit([X,Y], MaxLength, [RX,RY]) :-
                   getLength([X,Y], Length),
                   ( Length > MaxLength -> getNewLength([X,Y], MaxLength, [RX,RY]) ; RX is X, RY is Y ).

               steer([DSX, DSY], [SSX,SSY], [RSX,RSY]) :-
                   subtract([DSX, DSY], [SSX, SSY], [RX,RY]),
                   limit([RX,RY], 0.1, [RSX,RSY]).

               followTarget([[SPX,SPY],[SSX,SSY]],[[EPX,EPY],[ESX,ESY]],[RX,RY]) :-
                   add([EPX,EPY], [ESX,ESY], [NPX,NPY]),
                   unitVector([NPX,NPY],[SPX,SPY],[UVX,UVY]),
                   multiply([UVX,UVY], 2, [DSX,DSY]),
                   steer([DSX,DSY], [SSX,SSY], [RX,RY]).

               filter([_,_,SR], [], []):-!.
               filter([_,_,SR], [[EP,ES,ER,ET]|T], OUTL) :-
                   filter([_,_,SR], T, RL),
                   (ET \== 'AntiMatter' ->
                       ( SR > ER ->
                           (ER > 4.0 ->
                               OUTL = [[EP,ES,ER,ET]|RL];
                               OUTL = RL );
                           OUTL = RL );
                       OUTL = RL ).

               associateScoreToEnemy([SP,_,SR], [EP,ES,ER,ET], [[EP,ES,ER,ET],R]) :-
                   euclideanDistance(SP,EP,D),
                   divide(SR, D, R).

               associateScoreToEnemies(S, [H|[]], [R|[]]) :- associateScoreToEnemy(S, H, R),!.
               associateScoreToEnemies(S, [H|T], [R|TR]) :-
                   associateScoreToEnemy(S, H, R),
                   associateScoreToEnemies(S, T, TR),!.

               quicksort([],[]).
               quicksort([X|Xs],Ys):-
                   partition(Xs,X,Ls,Bs),
                   quicksort(Ls,LOs),
                   quicksort(Bs,BOs),
                   append(LOs,[X|BOs],Ys).

               partition([],_,[],[]).
               partition([[X,SCOREX]|Xs],[Y,SCOREY],[[X,SCOREX]|Ls],Bs):- SCOREX>SCOREY, !, partition(Xs,[Y,SCOREY],Ls,Bs).
               partition([X|Xs],Y,Ls,[X|Bs]):- partition(Xs,Y,Ls,Bs).

               findTarget(S,[H|T],RL) :-
                   filter(S, [H|T], FL),
                   associateScoreToEnemies(S, FL, EWS),
                   quicksort(EWS, RL).

               sentientCellBehaviour(S,L,[RX,RY]) :-
                   findTarget(S,L,RL),
                   [SP,SS,_] = S,
                   % considers the highest score result
                   [[[EP,ES,_,_],_]|K] = RL,
                   followTarget([SP,SS],[EP,ES],[RX,RY]).

               computeBehaviourOfSentientCells([],_,[]) :- !.
               computeBehaviourOfSentientCells([H|T],EL,[EA|TEA]) :-
                   sentientCellBehaviour(H,EL,EA),
                   computeBehaviourOfSentientCells(T,EL,TEA).
  """
}
